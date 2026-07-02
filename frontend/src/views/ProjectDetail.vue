<template>
  <div>
    <el-button text @click="$router.push('/')">← 返回项目列表</el-button>
    <h2 style="margin:10px 0">📋 {{ project.name }}
      <el-tag size="small" style="margin-left:10px">{{ project.status }}</el-tag>
    </h2>

    <el-tabs v-model="activeTab">
      <!-- Tab 1: Issue 看板 -->
      <el-tab-pane label="Issue" name="issues">
        <div style="margin-bottom:12px">
          <el-button type="primary" size="small" @click="showCreateIssue = true">+ 新建Issue</el-button>
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable size="small" style="width:140px;margin-left:10px">
            <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
          </el-select>
          <el-button size="small" style="margin-left:10px" @click="loadStandup" :loading="standupLoading">🤖 AI站报</el-button>
        </div>

        <el-table :data="issues" stripe @row-click="(row) => $router.push(`/projects/${project.id}/issues/${row.id}`)" style="cursor:pointer">
          <el-table-column prop="id" label="#" width="50" />
          <el-table-column prop="title" label="标题" />
          <el-table-column label="类型" width="90">
            <template #default="{row}"><el-tag :type="typeColor(row.type)" size="small">{{ row.type }}</el-tag></template>
          </el-table-column>
          <el-table-column label="优先级" width="80">
            <template #default="{row}"><el-tag :type="priColor(row.priority)" size="small">{{ row.priority }}</el-tag></template>
          </el-table-column>
          <el-table-column label="状态" width="110">
            <template #default="{row}"><el-tag size="small">{{ row.status }}</el-tag></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- Tab 2: Sprint -->
      <el-tab-pane label="Sprint" name="sprints">
        <el-button type="primary" size="small" @click="showCreateSprint = true" style="margin-bottom:12px">+ 新建Sprint</el-button>
        <el-row :gutter="16">
          <el-col :span="8" v-for="s in sprints" :key="s.id">
            <el-card shadow="hover" style="margin-bottom:12px">
              <h4>{{ s.name }}</h4>
              <p style="font-size:12px;color:#909399">{{ s.goal }}</p>
              <el-tag :type="s.status==='ACTIVE'?'success':s.status==='COMPLETED'?'info':'warning'" size="small">{{ s.status }}</el-tag>
              <div style="margin-top:8px">
                <el-button size="small" v-if="s.status==='PLANNING'" @click="startSprint(s.id)">开始</el-button>
                <el-button size="small" v-if="s.status==='ACTIVE'" type="success" @click="completeSprint(s.id)">完成</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- Tab 3: AI -->
      <el-tab-pane label="🤖 AI" name="ai">
        <el-card v-if="standup">
          <h3>📊 每日站报</h3>
          <p><strong>总结:</strong> {{ standup.summary }}</p>
          <p><strong>亮点:</strong></p><ul><li v-for="h in standup.highlights" :key="h">{{ h }}</li></ul>
          <p><strong>阻塞:</strong></p><ul><li v-for="b in standup.blockers" :key="b">{{ b }}</li></ul>
          <p><strong>明日计划:</strong></p><ul><li v-for="t in standup.tomorrowPlan" :key="t">{{ t }}</li></ul>
        </el-card>
        <el-empty v-else description="点击「AI站报」按钮生成" />
      </el-tab-pane>
    </el-tabs>

    <!-- 新建Issue弹窗 -->
    <el-dialog v-model="showCreateIssue" title="新建Issue" width="500px">
      <el-form :model="issueForm">
        <el-form-item label="标题"><el-input v-model="issueForm.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="issueForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="issueForm.type"><el-option v-for="t in ['BUG','FEATURE','TASK','IMPROVEMENT']" :key="t" :label="t" :value="t" /></el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="issueForm.priority"><el-option v-for="p in ['HIGHEST','HIGH','MEDIUM','LOW','LOWEST']" :key="p" :label="p" :value="p" /></el-select>
        </el-form-item>
        <el-form-item label="Sprint">
          <el-select v-model="issueForm.sprintId" clearable><el-option v-for="s in sprints" :key="s.id" :label="s.name" :value="s.id" /></el-select>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="showCreateIssue=false">取消</el-button><el-button type="primary" @click="createIssue">创建</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api'

const route = useRoute()
const project = ref({})
const issues = ref([])
const sprints = ref([])
const standup = ref(null)
const activeTab = ref('issues')
const filterStatus = ref('')
const showCreateIssue = ref(false)
const showCreateSprint = ref(false)
const standupLoading = ref(false)
const statuses = ['TODO','IN_PROGRESS','IN_REVIEW','TESTING','DONE']
const issueForm = reactive({ title:'', description:'', type:'TASK', priority:'MEDIUM', sprintId:null })

const load = async () => {
  const [pr, is, sp] = await Promise.all([
    api.getProject(route.params.id),
    api.listIssues(route.params.id, { status: filterStatus.value || undefined }),
    api.listSprints(route.params.id)
  ])
  project.value = pr.data; issues.value = is.data; sprints.value = sp.data
}

onMounted(load)
watch(filterStatus, load)

const createIssue = async () => {
  await api.createIssue(route.params.id, issueForm)
  showCreateIssue.value = false; load()
}
const loadStandup = async () => {
  standupLoading.value = true
  const res = await api.standup(route.params.id)
  standup.value = res.data; activeTab.value = 'ai'; standupLoading.value = false
}
const startSprint = async (sid) => { await api.startSprint(sid); load() }
const completeSprint = async (sid) => { /* use transition */ }
const typeColor = (t) => ({BUG:'danger',FEATURE:'success',TASK:'',IMPROVEMENT:'warning'}[t]||'')
const priColor = (p) => ({HIGHEST:'danger',HIGH:'warning',MEDIUM:'',LOW:'info',LOWEST:'info'}[p]||'')
</script>
