import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  Alert,
  Button,
  Card,
  Form,
  Input,
  Modal,
  Space,
  Table,
  Tag,
  Typography,
  message,
  DatePicker,
} from 'antd'
import type { TablePaginationConfig } from 'antd'
import { PlusOutlined, DeleteOutlined, QrcodeOutlined } from '@ant-design/icons'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import dayjs, { type Dayjs } from 'dayjs'
import { QRCodeSVG } from 'qrcode.react'
import { createUrl, deleteUrl, listUrls } from '../api/urlApi'
import { getErrorMessage } from '../api/http'
import type { UrlItem } from '../types/api'

export function UrlsPage() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [createOpen, setCreateOpen] = useState(false)
  const [qrUrl, setQrUrl] = useState<UrlItem | null>(null)
  const [form] = Form.useForm<{ originalUrl: string; expirationDate?: Dayjs }>()

  const query = useQuery({
    queryKey: ['urls', page, pageSize],
    queryFn: () => listUrls(page, pageSize, 'createdAt,desc'),
  })

  const createMutation = useMutation({
    mutationFn: createUrl,
    onSuccess: async () => {
      message.success('Short URL created')
      setCreateOpen(false)
      form.resetFields()
      await queryClient.invalidateQueries({ queryKey: ['urls'] })
    },
  })

  const deleteMutation = useMutation({
    mutationFn: deleteUrl,
    onSuccess: async () => {
      message.success('Short URL deleted')
      await queryClient.invalidateQueries({ queryKey: ['urls'] })
    },
  })

  const columns = useMemo(
    () => [
      {
        title: 'Short URL',
        dataIndex: 'shortUrl',
        key: 'shortUrl',
        render: (value: string, record: UrlItem) => (
          <Space direction="vertical" size={0}>
            <Typography.Link href={value} target="_blank" rel="noreferrer">
              {value}
            </Typography.Link>
            <Typography.Text type="secondary" copyable={{ text: record.shortCode }}>
              {record.shortCode}
            </Typography.Text>
          </Space>
        ),
      },
      {
        title: 'Original',
        dataIndex: 'originalUrl',
        key: 'originalUrl',
        ellipsis: true,
        render: (value: string) => (
          <Typography.Text ellipsis={{ tooltip: value }} style={{ maxWidth: 280 }}>
            {value}
          </Typography.Text>
        ),
      },
      {
        title: 'Clicks',
        dataIndex: 'clickCount',
        key: 'clickCount',
        width: 90,
      },
      {
        title: 'Status',
        dataIndex: 'isActive',
        key: 'isActive',
        width: 110,
        render: (active: boolean) => (
          <Tag color={active ? 'green' : 'default'}>{active ? 'ACTIVE' : 'INACTIVE'}</Tag>
        ),
      },
      {
        title: 'Created',
        dataIndex: 'createdAt',
        key: 'createdAt',
        width: 180,
        render: (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm'),
      },
      {
        title: 'Actions',
        key: 'actions',
        width: 220,
        render: (_: unknown, record: UrlItem) => (
          <Space>
            <Link to={`/urls/${record.id}`}>
              <Button size="small">Detail</Button>
            </Link>
            <Button
              size="small"
              icon={<QrcodeOutlined />}
              onClick={() => setQrUrl(record)}
            />
            <Button
              size="small"
              danger
              icon={<DeleteOutlined />}
              loading={deleteMutation.isPending}
              onClick={() => {
                Modal.confirm({
                  title: 'Delete this short URL?',
                  content: 'This performs a soft delete. The redirect will stop working.',
                  okText: 'Delete',
                  okButtonProps: { danger: true },
                  onOk: () => deleteMutation.mutateAsync(record.id),
                })
              }}
            />
          </Space>
        ),
      },
    ],
    [deleteMutation],
  )

  const pagination: TablePaginationConfig = {
    current: page + 1,
    pageSize,
    total: query.data?.totalElements ?? 0,
    showSizeChanger: true,
    onChange: (nextPage, nextSize) => {
      setPage(nextPage - 1)
      setPageSize(nextSize)
    },
  }

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Space style={{ width: '100%', justifyContent: 'space-between' }}>
        <div>
          <Typography.Title level={3} style={{ marginBottom: 4 }}>
            My URLs
          </Typography.Title>
          <Typography.Text type="secondary">
            Create short links, copy them, and generate QR codes.
          </Typography.Text>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setCreateOpen(true)}>
          Create URL
        </Button>
      </Space>

      {query.isError && (
        <Alert type="error" showIcon message={getErrorMessage(query.error, 'Failed to load URLs')} />
      )}

      <Card>
        <Table
          rowKey="id"
          loading={query.isLoading}
          columns={columns}
          dataSource={query.data?.content ?? []}
          pagination={pagination}
        />
      </Card>

      <Modal
        title="Create short URL"
        open={createOpen}
        onCancel={() => setCreateOpen(false)}
        footer={null}
        destroyOnHidden
      >
        {createMutation.isError && (
          <Alert
            type="error"
            showIcon
            style={{ marginBottom: 16 }}
            message={getErrorMessage(createMutation.error)}
          />
        )}
        <Form
          form={form}
          layout="vertical"
          onFinish={(values) =>
            createMutation.mutate({
              originalUrl: values.originalUrl,
              expirationDate: values.expirationDate
                ? values.expirationDate.toISOString()
                : null,
            })
          }
        >
          <Form.Item
            label="Original URL"
            name="originalUrl"
            rules={[
              { required: true, message: 'URL is required' },
              {
                pattern: /^(https?:\/\/).+/i,
                message: 'URL must start with http:// or https://',
              },
            ]}
          >
            <Input placeholder="https://example.com/very/long/path" />
          </Form.Item>
          <Form.Item label="Expiration (optional)" name="expirationDate">
            <DatePicker
              showTime
              style={{ width: '100%' }}
              disabledDate={(current) => !!current && current.isBefore(dayjs().startOf('day'))}
            />
          </Form.Item>
          <Button type="primary" htmlType="submit" block loading={createMutation.isPending}>
            Create
          </Button>
        </Form>
      </Modal>

      <Modal
        title="QR code"
        open={Boolean(qrUrl)}
        onCancel={() => setQrUrl(null)}
        footer={null}
      >
        {qrUrl && (
          <Space direction="vertical" align="center" style={{ width: '100%' }}>
            <QRCodeSVG value={qrUrl.shortUrl} size={200} />
            <Typography.Paragraph copyable>{qrUrl.shortUrl}</Typography.Paragraph>
          </Space>
        )}
      </Modal>
    </Space>
  )
}
