import { http } from './http'
import type { ApiResponse, CreateUrlPayload, PageResponse, UrlItem } from '../types/api'

export async function listUrls(page = 0, size = 10, sort = 'createdAt,desc'): Promise<PageResponse<UrlItem>> {
  const { data } = await http.get<ApiResponse<PageResponse<UrlItem>>>('/api/v1/urls', {
    params: { page, size, sort },
  })
  if (!data.responseData) {
    throw new Error(data.message ?? 'Failed to load URLs')
  }
  return data.responseData
}

export async function getUrl(id: number): Promise<UrlItem> {
  const { data } = await http.get<ApiResponse<UrlItem>>(`/api/v1/urls/${id}`)
  if (!data.responseData) {
    throw new Error(data.message ?? 'Failed to load URL')
  }
  return data.responseData
}

export async function createUrl(payload: CreateUrlPayload): Promise<UrlItem> {
  const { data } = await http.post<ApiResponse<UrlItem>>('/api/v1/urls', payload)
  if (!data.responseData) {
    throw new Error(data.message ?? 'Failed to create URL')
  }
  return data.responseData
}

export async function deleteUrl(id: number): Promise<void> {
  await http.delete(`/api/v1/urls/${id}`)
}
