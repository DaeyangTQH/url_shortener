import { useEffect, useMemo } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Alert, Card, Spin, Typography } from 'antd'
import { setTokens } from '../lib/authStorage'

export function OAuthCallbackPage() {
  const [params] = useSearchParams()
  const navigate = useNavigate()

  const accessToken = params.get('accessToken')
  const refreshToken = params.get('refreshToken')
  const error = params.get('error')

  const errorMessage = useMemo(() => {
    if (!error) return null
    if (error.startsWith('error.')) {
      return error
    }
    return error
  }, [error])

  useEffect(() => {
    if (errorMessage) {
      return
    }
    if (accessToken && refreshToken) {
      setTokens(accessToken, refreshToken)
      navigate('/urls', { replace: true })
    }
  }, [accessToken, refreshToken, errorMessage, navigate])

  if (errorMessage) {
    return (
      <div className="auth-shell">
        <Card className="auth-card" title="Google sign-in failed">
          <Alert type="error" showIcon message={errorMessage} />
          <Typography.Link href="/login" style={{ display: 'inline-block', marginTop: 16 }}>
            Back to login
          </Typography.Link>
        </Card>
      </div>
    )
  }

  if (!accessToken || !refreshToken) {
    return (
      <div className="auth-shell">
        <Card className="auth-card" title="Google sign-in">
          <Alert type="warning" showIcon message="Missing tokens in callback URL." />
          <Typography.Link href="/login" style={{ display: 'inline-block', marginTop: 16 }}>
            Back to login
          </Typography.Link>
        </Card>
      </div>
    )
  }

  return (
    <div className="auth-shell">
      <Spin tip="Completing Google sign-in..." size="large" />
    </div>
  )
}
