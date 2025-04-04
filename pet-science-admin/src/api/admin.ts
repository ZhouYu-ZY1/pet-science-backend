import request from '@/utils/request'

// 用户登录
export function login(data: { account: string; password: string }) {
    return request({
      url: '/admin/login',
      method: 'post',
      params: data
    })
  }