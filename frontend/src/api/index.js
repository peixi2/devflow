import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(res => res.data,
  err => { ElMessage.error(err.response?.data?.message || '请求失败'); return Promise.reject(err) })

export default {
  // Auth
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  // Projects
  listProjects: () => api.get('/projects'),
  getProject: (id) => api.get(`/projects/${id}`),
  createProject: (data) => api.post('/projects', data),
  deleteProject: (id) => api.delete(`/projects/${id}`),
  addMember: (id, data) => api.post(`/projects/${id}/members`, data),
  // Sprints
  listSprints: (pid) => api.get(`/projects/${pid}/sprints`),
  createSprint: (pid, data) => api.post(`/projects/${pid}/sprints`, data),
  startSprint: (pid, sid) => api.put(`/projects/${pid}/sprints/${sid}/start`),
  completeSprint: (pid, sid) => api.put(`/projects/${pid}/sprints/${sid}/complete`),
  // Issues
  listIssues: (pid, params) => api.get(`/projects/${pid}/issues`, { params }),
  getIssue: (pid, id) => api.get(`/projects/${pid}/issues/${id}`),
  createIssue: (pid, data) => api.post(`/projects/${pid}/issues`, data),
  transitionIssue: (pid, id, to) => api.post(`/projects/${pid}/issues/${id}/transition?to=${to}`),
  getComments: (pid, id) => api.get(`/projects/${pid}/issues/${id}/comments`),
  addComment: (pid, id, data) => api.post(`/projects/${pid}/issues/${id}/comments`, data),
  // AI
  classifyIssue: (id) => api.post(`/ai/issues/${id}/classify`),
  findSimilar: (id) => api.get(`/ai/issues/${id}/similar`),
  standup: (pid) => api.get(`/ai/projects/${pid}/standup`),
  // Notifications
  listNotifications: () => api.get('/notifications'),
  unreadCount: () => api.get('/notifications/unread-count'),
}
