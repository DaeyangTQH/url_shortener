import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios'
import type { ApiResponse, AuthenticationResponse, ErrorResponse } from '../types/api'
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from '../lib/authStorage'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export const http = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getAccessToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let refreshPromise: Promise<string | null> | null = null

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    return null
  }

  try {
    const { data } = await axios.post<ApiResponse<AuthenticationResponse>>(
      `${API_BASE_URL}/api/v1/auth/refresh`,
      { refreshToken },
    )
    const auth = data.responseData
    if (!auth?.accessToken || !auth.refreshToken) {
      return null
    }
    setTokens(auth.accessToken, auth.refreshToken)
    return auth.accessToken
  } catch {
    clearTokens()
    return null
  }
}

http.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ErrorResponse>) => {
    const original = error.config as InternalAxiosRequestConfig & { _retry?: boolean }
    if (error.response?.status !== 401 || !original || original._retry) {
      return Promise.reject(error)
    }

    // Don't try refresh on auth endpoints themselves
    if (original.url?.includes('/api/v1/auth/')) {
      return Promise.reject(error)
    }

    original._retry = true
    refreshPromise ??= refreshAccessToken().finally(() => {
      refreshPromise = null
    })

    const newToken = await refreshPromise
    if (!newToken) {
      clearTokens()
      window.location.assign('/login')
      return Promise.reject(error)
    }

    original.headers.Authorization = `Bearer ${newToken}`
    return http(original)
  },
)

export function getErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  if (axios.isAxiosError<ErrorResponse>(error)) {
    return error.response?.data?.message ?? error.message ?? fallback
  }
  if (error instanceof Error) {
    return error.message
  }
  return fallback
}
