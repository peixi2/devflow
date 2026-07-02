import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/', name: 'Projects', component: () => import('../views/Projects.vue'), meta: { auth: true } },
  { path: '/projects/:id', name: 'ProjectDetail', component: () => import('../views/ProjectDetail.vue'), meta: { auth: true } },
  { path: '/projects/:pid/issues/:iid', name: 'IssueDetail', component: () => import('../views/IssueDetail.vue'), meta: { auth: true } },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.auth && !token) next('/login')
  else next()
})

export default router
