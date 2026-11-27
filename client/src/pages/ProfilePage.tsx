import { Alert, Card, Descriptions, Spin, Tag, Typography } from 'antd'
import { useQuery } from '@tanstack/react-query'
import { getMyProfile } from '../api/userApi'
import { getErrorMessage } from '../api/http'

export function ProfilePage() {
  const query = useQuery({
    queryKey: ['profile'],
    queryFn: getMyProfile,
  })

  if (query.isLoading) {
    return <Spin />
  }

  if (query.isError || !query.data) {
    return <Alert type="error" showIcon message={getErrorMessage(query.error, 'Failed to load profile')} />
  }

  const profile = query.data

  return (
    <>
      <Typography.Title level={3}>Profile</Typography.Title>
      <Card>
        <Descriptions bordered column={1}>
          <Descriptions.Item label="Username">{profile.username}</Descriptions.Item>
          <Descriptions.Item label="Email">{profile.email}</Descriptions.Item>
          <Descriptions.Item label="Role">{profile.roleName}</Descriptions.Item>
          <Descriptions.Item label="Status">
            <Tag color={profile.status === 'ACTIVE' ? 'green' : 'orange'}>{profile.status}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Email verified">
            {profile.emailVerified ? 'Yes' : 'No'}
          </Descriptions.Item>
        </Descriptions>
      </Card>
    </>
  )
}
