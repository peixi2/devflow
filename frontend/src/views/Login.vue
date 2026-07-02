<template>
  <div class="login-bg">
    <el-card class="login-card">
      <h2 style="text-align:center;margin-bottom:20px">🚀 DevFlow 登录</h2>
      <el-form :model="form" label-width="70px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="admin" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" @keyup.enter="login" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" :loading="loading" style="width:100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="register" :loading="loading" style="width:100%">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const router = useRouter()
const form = reactive({ username: 'admin', password: 'admin123' })
const loading = ref(false)

const login = async () => {
  loading.value = true
  try {
    const res = await api.login(form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data))
    router.push('/')
    ElMessage.success('登录成功')
  } catch (e) {} finally { loading.value = false }
}

const register = async () => {
  if (form.username.length < 3 || form.password.length < 6) {
    ElMessage.warning('用户名3位+密码6位以上')
    return
  }
  loading.value = true
  try {
    await api.register({ username: form.username, password: form.password, nickname: form.username })
    ElMessage.success('注册成功，请登录')
  } catch (e) {} finally { loading.value = false }
}
</script>

<style scoped>
.login-bg { display:flex; justify-content:center; align-items:center; height:100vh; background:linear-gradient(135deg,#667eea 0%,#764ba2 100%) }
.login-card { width:380px }
</style>
