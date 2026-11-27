import { Link, useNavigate } from 'react-router-dom'
import { Alert, Button, Card, Form, Input, Space, Typography } from 'antd'
import { useMutation } from '@tanstack/react-query'
import { register } from '../api/authApi'
import { getErrorMessage } from '../api/http'
import { setTokens } from '../lib/authStorage'

export function RegisterPage() {
  const navigate = useNavigate()
  const [form] = Form.useForm()

  const mutation = useMutation({
    mutationFn: (values: { username: string; email: string; password: string }) =>
      register(values.username, values.email, values.password),
    onSuccess: (auth) => {
      setTokens(auth.accessToken, auth.refreshToken)
      navigate('/urls', { replace: true })
    },
  })

  return (
    <div className="auth-shell">
      <Card className="auth-card" title="Create account">
        <Typography.Paragraph type="secondary">
          Register a local account to create and manage short URLs.
        </Typography.Paragraph>

        {mutation.isError && (
          <Alert
            type="error"
            showIcon
            style={{ marginBottom: 16 }}
            message={getErrorMessage(mutation.error, 'Registration failed')}
          />
        )}

        <Form
          form={form}
          layout="vertical"
          onFinish={(values) => mutation.mutate(values)}
          requiredMark={false}
        >
          <Form.Item
            label="Username"
            name="username"
            rules={[
              { required: true, message: 'Username is required' },
              { min: 3, max: 50, message: 'Username must be 3-50 characters' },
            ]}
          >
            <Input placeholder="haichan" autoComplete="username" />
          </Form.Item>
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
            rules={[
              { required: true, message: 'Password is required' },
              {
                pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/,
                message: 'Use upper, lower, number, and special character (min 8)',
              },
            ]}
          >
            <Input.Password placeholder="Password@123" autoComplete="new-password" />
          </Form.Item>
          <Button type="primary" htmlType="submit" block loading={mutation.isPending}>
            Create account
          </Button>
        </Form>

        <Space style={{ marginTop: 16 }}>
          <Typography.Text type="secondary">Already have an account?</Typography.Text>
          <Link to="/login">Sign in</Link>
        </Space>
      </Card>
    </div>
  )
}
