import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { Layout, Menu, Button, Space, Typography, theme } from 'antd'
import { LinkOutlined, LogoutOutlined, UserOutlined } from '@ant-design/icons'
import { useMutation } from '@tanstack/react-query'
import { logout } from '../api/authApi'
import { clearTokens, getRefreshToken } from '../lib/authStorage'

const { Header, Content } = Layout

export function AppLayout() {
  const navigate = useNavigate()
  const location = useLocation()
  const { token } = theme.useToken()

  const logoutMutation = useMutation({
    mutationFn: async () => {
      const refreshToken = getRefreshToken()
      if (refreshToken) {
        try {
          await logout(refreshToken)
        } catch {
          // Clear local session even if API logout fails
        }
      }
      clearTokens()
    },
    onSettled: () => navigate('/login', { replace: true }),
  })

  const selectedKey = location.pathname.startsWith('/profile') ? 'profile' : 'urls'

  return (
    <Layout style={{ minHeight: '100vh', background: token.colorBgLayout }}>
      <Header
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          paddingInline: 24,
          background: '#0f172a',
        }}
      >
        <Space size="large">
          <Typography.Title level={4} style={{ color: '#fff', margin: 0 }}>
            Shortly
          </Typography.Title>
          <Menu
            theme="dark"
            mode="horizontal"
            selectedKeys={[selectedKey]}
            style={{ background: 'transparent', minWidth: 280, borderBottom: 'none' }}
            items={[
              {
                key: 'urls',
                icon: <LinkOutlined />,
                label: <Link to="/urls">My URLs</Link>,
              },
              {
                key: 'profile',
                icon: <UserOutlined />,
                label: <Link to="/profile">Profile</Link>,
              },
            ]}
          />
        </Space>
        <Button
          type="text"
          icon={<LogoutOutlined />}
          loading={logoutMutation.isPending}
          onClick={() => logoutMutation.mutate()}
          style={{ color: '#e2e8f0' }}
        >
          Logout
        </Button>
      </Header>
      <Content style={{ padding: '24px 16px', maxWidth: 1100, width: '100%', margin: '0 auto' }}>
        <Outlet />
      </Content>
    </Layout>
  )
}
