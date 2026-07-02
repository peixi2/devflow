<template>
  <div>
    <el-button text @click="$router.back()">← 返回</el-button>
    <el-card v-if="issue.id" style="margin-top:10px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <h3>[{{ issue.type }}] {{ issue.title }}</h3>
          <div>
            <el-tag :type="issue.status==='DONE'?'success':''" style="margin-right:8px">{{ issue.status }}</el-tag>
            <el-button size="small" v-for="next in nextStatuses" :key="next" @click="transition(next)">→ {{ next }}</el-button>
            <el-button size="small" type="warning" @click="classify" :loading="aiLoading" style="margin-left:8px">🤖 AI分类</el-button>
            <el-button size="small" @click="findSimilar" :loading="aiLoading" style="margin-left:4px">🔍 相似检索</el-button>
          </div>
        </div>
      </template>
      <p style="color:#909399">{{ issue.description || '暂无描述' }}</p>
      <el-divider />
      <p><strong>优先级:</strong> {{ issue.priority }} &nbsp;|&nbsp;
         <strong>AI标签:</strong> <el-tag v-for="t in aiTags" :key="t" size="small" style="margin-right:4px">{{ t }}</el-tag>
      </p>

      <!-- AI分析结果 -->
      <el-alert v-if="aiResult" :title="'AI分析'" :description="aiResult" type="info" show-icon closable style="margin-top:10px" />

      <el-divider />
      <h4>💬 评论</h4>
      <div v-for="c in comments" :key="c.id" style="padding:8px;border-bottom:1px solid #ebeef5">
        <strong>用户#{{ c.userId }}</strong>
        <span style="color:#909399;font-size:12px;margin-left:8px">{{ c.createdAt }}</span>
        <p style="margin-top:4px">{{ c.content }}</p>
      </div>
      <el-input v-model="newComment" placeholder="添加评论..." style="margin-top:10px">
        <template #append><el-button @click="addComment">发送</el-button></template>
      </el-input>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api'

const route = useRoute()
const { pid, iid } = route.params
const issue = ref({})
const comments = ref([])
const newComment = ref('')
const aiResult = ref('')
const aiLoading = ref(false)

const flows = { TODO: ['IN_PROGRESS'], IN_PROGRESS: ['IN_REVIEW','TODO'], IN_REVIEW: ['TESTING','IN_PROGRESS'], TESTING: ['DONE','IN_PROGRESS'], DONE: ['TODO'] }
const nextStatuses = computed(() => flows[issue.value.status] || [])
const aiTags = computed(() => issue.value.aiTags ? issue.value.aiTags.split(',') : [])

const load = async () => {
  const [ir, cr] = await Promise.all([api.getIssue(pid, iid), api.getComments(pid, iid)])
  issue.value = ir.data; comments.value = cr.data
}
onMounted(load)

const transition = async (to) => { await api.transitionIssue(pid, iid, to); load() }
const addComment = async () => {
  if (!newComment.value.trim()) return
  await api.addComment(pid, iid, { content: newComment.value })
  newComment.value = ''; load()
}
const classify = async () => {
  aiLoading.value = true
  const res = await api.classifyIssue(iid)
  aiResult.value = JSON.stringify(res.data, null, 2); aiLoading.value = false; load()
}
const findSimilar = async () => {
  aiLoading.value = true
  const res = await api.findSimilar(iid)
  aiResult.value = res.data?.length ? '相似Issue: ' + res.data.map(s => `#${s.issueId} ${s.title} (${s.similarity})`).join(', ') : '未找到相似Issue'
  aiLoading.value = false
}
</script>
