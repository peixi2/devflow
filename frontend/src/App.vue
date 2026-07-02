<template>
  <div id="app-layout">
    <el-container v-if="$route.meta.auth">
      <el-aside width="200px" style="background:#1d1e2c;min-height:100vh;padding-top:20px">
        <div style="color:#fff;text-align:center;font-size:20px;font-weight:bold;margin-bottom:30px">🚀 DevFlow</div>
        <el-menu :default-active="$route.path" router background-color="#1d1e2c" text-color="#8b8fa3" active-text-color="#409eff" style="border-right:none">
          <el-menu-item index="/"><el-icon><FolderOpened /></el-icon> 我的项目</el-menu-item>
        </el-menu>
        <div style="position:absolute;bottom:20px;left:20px">
          <el-button text style="color:#8b8fa3" @click="logout">退出登录</el-button>
        </div>
      </el-aside>
      <el-main style="background:#f5f7fa;min-height:100vh"><router-view /></el-main>
    </el-container>
    <router-view v-else />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const logout = () => { localStorage.clear(); router.push('/login') }
</script>
