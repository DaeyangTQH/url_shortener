import { http } from './http'
import type { ApiResponse, AuthenticationResponse } from '../types/api'

export async function login(email: string, password: string): Promise<AuthenticationResponse> {
  const { data } = await http.post<ApiResponse<AuthenticationResponse>>('/api/v1/auth/login', {
    email,
    password,
  })
  if (!data.responseData) {
    throw new Error(data.message ?? 'Login failed')
  }
  return data.responseData
}

export async function register(
  username: string,
  email: string,
  password: string,
): Promise<AuthenticationResponse> {
  const { data } = await http.post<ApiResponse<AuthenticationResponse>>('/api/v1/auth/register', {
    username,
    email,
    password,
  })
  if (!data.responseData) {
    throw new Error(data.message ?? 'Register failed')
  }
  return data.responseData
}

export async function logout(refreshToken: string): Promise<void> {
  await http.post('/api/v1/auth/logout', { refreshToken })
}
