import { Link, useNavigate } from 'react-router-dom'
import { Alert, Button, Card, Divider, Form, Input, Typography, Space } from 'antd'
import { GoogleOutlined } from '@ant-design/icons'
import { useMutation } from '@tanstack/react-query'
import { login } from '../api/authApi'
import { getErrorMessage } from '../api/http'
import { setTokens } from '../lib/authStorage'

const GOOGLE_URL = import.meta.env.VITE_OAUTH_GOOGLE_URL ?? 'http://localhost:8080/oauth2/authorization/google'

export function LoginPage() {
  const navigate = useNavigate()
  const [form] = Form.useForm()

  const mutation = useMutation({
    mutationFn: (values: { email: string; password: string }) => login(values.email, values.password),
    onSuccess: (auth) => {
      setTokens(auth.accessToken, auth.refreshToken)
      navigate('/urls', { replace: true })
    },
  })

  return (
    <div className="auth-shell">
      <Card className="auth-card" title="Sign in">
        <Typography.Paragraph type="secondary">
          Manage short links with your local account or Google.
        </Typography.Paragraph>

        {mutation.isError && (
          <Alert
            type="error"
            showIcon
            style={{ marginBottom: 16 }}
            message={getErrorMessage(mutation.error, 'Login failed')}
          />
        )}

        <Form
          form={form}
          layout="vertical"
          onFinish={(values) => mutation.mutate(values)}
          requiredMark={false}
        >
          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: 'Email is required' },
              { type: 'email', message: 'Enter a valid email' },
            ]}
          >
            <Input placeholder="you@example.com" autoComplete="email" />
          </Form.Item>
          <Form.Item
            label="Password"
            name="password"
            rules={[{ required: true, message: 'Password is required' }]}
          >
            <Input.Password placeholder="Your password" autoComplete="current-password" />
          </Form.Item>
          <Button type="primary" htmlType="submit" block loading={mutation.isPending}>
            Sign in
          </Button>
        </Form>

        <Divider plain>or</Divider>

        <Button
          block
          icon={<GoogleOutlined />}
          onClick={() => {
            window.location.href = GOOGLE_URL
          }}
        >
          Continue with Google
        </Button>

        <Space style={{ marginTop: 16 }}>
          <Typography.Text type="secondary">No account?</Typography.Text>
          <Link to="/register">Create one</Link>
        </Space>
      </Card>
    </div>
  )
}
