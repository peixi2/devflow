<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
      <h2>📁 我的项目</h2>
      <el-button type="primary" @click="showCreate = true">+ 创建项目</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :span="8" v-for="p in projects" :key="p.id" style="margin-bottom:16px">
        <el-card shadow="hover" @click="$router.push(`/projects/${p.id}`)" style="cursor:pointer">
          <h3>{{ p.name }}</h3>
          <p style="color:#909399;font-size:13px">{{ p.description || '暂无描述' }}</p>
          <el-tag :type="p.status === 'ACTIVE' ? 'success' : 'info'" size="small">{{ p.status }}</el-tag>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!projects.length" description="还没有项目" />

    <el-dialog v-model="showCreate" title="创建项目" width="400px">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="create">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../api'
const projects = ref([])
const showCreate = ref(false)
const form = reactive({ name: '', description: '' })

onMounted(async () => {
  const res = await api.listProjects()
  projects.value = res.data || []
})

const create = async () => {
  await api.createProject(form)
  showCreate.value = false
  form.name = ''; form.description = ''
  const res = await api.listProjects()
  projects.value = res.data || []
}
</script>
