export interface ApiResponse<T> {
  message?: string
  responseData?: T
}

export interface ErrorResponse {
  message: string
  error: string
  timestamp: string
  path: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface AuthenticationResponse {
  authenticated: boolean
  accessToken: string
  refreshToken: string
}

export interface Profile {
  id: string
  username: string
  email: string
  roleName: string
  status: string
  avatarImageUrl?: string
  emailVerified?: boolean
}

export interface UrlItem {
  id: number
  shortCode: string
  shortUrl: string
  originalUrl: string
  expirationDate?: string | null
  isActive: boolean
  clickCount: number
  createdAt: string
}

export interface CreateUrlPayload {
  originalUrl: string
  expirationDate?: string | null
}
