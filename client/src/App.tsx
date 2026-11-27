import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { App as AntApp, ConfigProvider } from 'antd'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { AppLayout } from './components/AppLayout'
import { ProtectedRoute } from './components/ProtectedRoute'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { OAuthCallbackPage } from './pages/OAuthCallbackPage'
import { UrlsPage } from './pages/UrlsPage'
import { UrlDetailPage } from './pages/UrlDetailPage'
import { ProfilePage } from './pages/ProfilePage'
import { isAuthenticated } from './lib/authStorage'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})

function GuestOnly({ children }: { children: React.ReactNode }) {
  if (isAuthenticated()) {
    return <Navigate to="/urls" replace />
  }
  return children
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ConfigProvider
        theme={{
          token: {
            colorPrimary: '#0f766e',
            borderRadius: 10,
            fontFamily: '"Segoe UI", "Helvetica Neue", Arial, sans-serif',
          },
        }}
      >
        <AntApp>
          <BrowserRouter>
            <Routes>
              <Route
                path="/login"
                element={
                  <GuestOnly>
                    <LoginPage />
                  </GuestOnly>
                }
              />
              <Route
                path="/register"
                element={
                  <GuestOnly>
                    <RegisterPage />
                  </GuestOnly>
                }
              />
              <Route path="/oauth/callback" element={<OAuthCallbackPage />} />

              <Route element={<ProtectedRoute />}>
                <Route element={<AppLayout />}>
                  <Route path="/urls" element={<UrlsPage />} />
                  <Route path="/urls/:id" element={<UrlDetailPage />} />
                  <Route path="/profile" element={<ProfilePage />} />
                </Route>
              </Route>

              <Route path="/" element={<Navigate to={isAuthenticated() ? '/urls' : '/login'} replace />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </BrowserRouter>
        </AntApp>
      </ConfigProvider>
    </QueryClientProvider>
  )
}
