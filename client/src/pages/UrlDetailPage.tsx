import { Link, useParams } from 'react-router-dom'
import { Alert, Button, Card, Descriptions, Space, Spin, Tag, Typography } from 'antd'
import { ArrowLeftOutlined } from '@ant-design/icons'
import { useQuery } from '@tanstack/react-query'
import dayjs from 'dayjs'
import { QRCodeSVG } from 'qrcode.react'
import { getUrl } from '../api/urlApi'
import { getErrorMessage } from '../api/http'

export function UrlDetailPage() {
  const { id } = useParams()
  const numericId = Number(id)

  const query = useQuery({
    queryKey: ['url', numericId],
    queryFn: () => getUrl(numericId),
    enabled: Number.isFinite(numericId) && numericId > 0,
  })

  if (!Number.isFinite(numericId) || numericId <= 0) {
    return <Alert type="error" message="Invalid URL id" />
  }

  if (query.isLoading) {
    return <Spin />
  }

  if (query.isError || !query.data) {
    return (
      <Space direction="vertical">
        <Alert type="error" showIcon message={getErrorMessage(query.error, 'URL not found')} />
        <Link to="/urls">Back to list</Link>
      </Space>
    )
  }

  const item = query.data

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Link to="/urls">
        <Button type="link" icon={<ArrowLeftOutlined />} style={{ paddingInline: 0 }}>
          Back to list
        </Button>
      </Link>

      <Typography.Title level={3} style={{ margin: 0 }}>
        URL detail
      </Typography.Title>

      <Card>
        <Descriptions bordered column={1} size="middle">
          <Descriptions.Item label="Short URL">
            <Typography.Link href={item.shortUrl} target="_blank" rel="noreferrer" copyable>
              {item.shortUrl}
            </Typography.Link>
          </Descriptions.Item>
          <Descriptions.Item label="Original URL">
            <Typography.Paragraph copyable style={{ marginBottom: 0 }}>
              {item.originalUrl}
            </Typography.Paragraph>
          </Descriptions.Item>
          <Descriptions.Item label="Short code">{item.shortCode}</Descriptions.Item>
          <Descriptions.Item label="Status">
            <Tag color={item.isActive ? 'green' : 'default'}>
              {item.isActive ? 'ACTIVE' : 'INACTIVE'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Clicks">{item.clickCount}</Descriptions.Item>
          <Descriptions.Item label="Created">
            {dayjs(item.createdAt).format('YYYY-MM-DD HH:mm:ss')}
          </Descriptions.Item>
          <Descriptions.Item label="Expires">
            {item.expirationDate
              ? dayjs(item.expirationDate).format('YYYY-MM-DD HH:mm:ss')
              : 'Never'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="QR code">
        <Space direction="vertical" align="center" style={{ width: '100%' }}>
          <QRCodeSVG value={item.shortUrl} size={220} />
          <Typography.Text type="secondary">Scan to open the short URL</Typography.Text>
        </Space>
      </Card>
    </Space>
  )
}
