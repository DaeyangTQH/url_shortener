import { http } from './http'
import type { ApiResponse, Profile } from '../types/api'

export async function getMyProfile(): Promise<Profile> {
  const { data } = await http.get<ApiResponse<Profile>>('/api/v1/user/me')
  if (!data.responseData) {
    throw new Error(data.message ?? 'Failed to load profile')
  }
  return data.responseData
}
