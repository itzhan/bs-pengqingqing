<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NAvatar, NButton, NForm, NFormItem, NInput, NModal, NTag, useMessage } from 'naive-ui'
import { getMasterProfile } from '@/api/master'
import { applyRelation, getMyMasters } from '@/api/relation'
import { useUserStore } from '@/stores/user'
import type { MasterProfile, Relation } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const profile = ref<MasterProfile | null>(null)
const relation = ref<Relation | null>(null)
const applyVisible = ref(false)
const applying = ref(false)
const applyForm = ref({
  applyReason: ''
})

onMounted(async () => {
  try {
    const res = await getMasterProfile(Number(route.params.id))
    profile.value = res.data
    if (userStore.isLoggedIn && userStore.isApprentice) {
      await loadRelation()
    }
  } finally {
  }
})

async function loadRelation() {
  const relationRes = await getMyMasters({ masterId: Number(route.params.id) })
  relation.value = (relationRes.data || [])[0] || null
}

function openApply() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (!userStore.isApprentice) {
    message.warning('只有徒弟角色可以申请拜师')
    return
  }
  if (relation.value && (relation.value.status === 0 || relation.value.status === 1)) {
    message.warning(relation.value.status === 0 ? '你已提交申请，请等待审核' : '你已与该师傅建立关系')
    return
  }
  applyVisible.value = true
}

async function submitApply() {
  if (!applyForm.value.applyReason.trim()) {
    message.warning('请填写拜师理由')
    return
  }
  applying.value = true
  try {
    await applyRelation({
      masterId: Number(route.params.id),
      heritageProjectId: profile.value?.heritageProjectId || null,
      applyReason: applyForm.value.applyReason.trim()
    })
    applyVisible.value = false
    applyForm.value.applyReason = ''
    message.success('拜师申请已提交，请等待师傅审核')
    await loadRelation()
  } catch (e: any) {
    message.error(e.message || '申请失败')
  } finally {
    applying.value = false
  }
}

function relationButtonText() {
  if (!relation.value) return '申请拜师'
  return ['申请已提交', '已建立关系', '重新申请', '重新申请'][relation.value.status] || '申请拜师'
}

function relationStatusType(status?: number): any {
  if (status == null) return 'default'
  return ['warning', 'success', 'error', 'default'][status]
}

function relationStatusText(status?: number) {
  if (status == null) return ''
  return ['待审核', '已通过', '已拒绝', '已解除'][status] || ''
}
</script>

<template>
  <div class="container" style="padding-top:32px; padding-bottom:64px;">
    <n-button text @click="router.back()" style="margin-bottom:24px;">← 返回</n-button>
    <template v-if="profile">
      <div class="profile-header">
        <n-avatar :size="96" round :src="profile.avatar || undefined">
          {{ profile.realName?.charAt(0) || '师' }}
        </n-avatar>
        <div class="profile-header-info">
          <h1>{{ profile.realName || '传承师傅' }}</h1>
          <p class="profile-title" v-if="profile.title">{{ profile.title }}</p>
          <div class="profile-tags">
            <n-tag v-if="profile.careerYears" :bordered="false" size="small">从艺 {{ profile.careerYears }} 年</n-tag>
            <n-tag v-if="profile.honor" type="warning" :bordered="false" size="small">{{ profile.honor }}</n-tag>
            <n-tag v-if="profile.projectName" type="info" :bordered="false" size="small">{{ profile.projectName }}</n-tag>
          </div>
          <div v-if="userStore.isApprentice" style="margin-top:16px;">
            <n-button type="primary" @click="openApply">{{ relationButtonText() }}</n-button>
            <n-tag
              v-if="relation"
              :type="relationStatusType(relation.status)"
              :bordered="false"
              style="margin-left:12px;"
            >
              {{ relationStatusText(relation.status) }}
            </n-tag>
          </div>
        </div>
      </div>

      <div class="profile-sections">
        <div class="profile-section" v-if="profile.bio">
          <h3>个人简介</h3>
          <p>{{ profile.bio }}</p>
        </div>
        <div class="profile-section" v-if="profile.specialties">
          <h3>擅长领域</h3>
          <p>{{ profile.specialties }}</p>
        </div>
        <div class="profile-section" v-if="profile.careerHistory">
          <h3>从艺经历</h3>
          <p style="white-space:pre-wrap;">{{ profile.careerHistory }}</p>
        </div>
      </div>
    </template>

    <n-modal v-model:show="applyVisible" preset="card" style="max-width:560px;" title="提交拜师申请">
      <n-form :model="applyForm" label-placement="top">
        <n-form-item label="意向传承项目">
          <n-input :value="profile?.projectName || '未设置项目'" disabled />
        </n-form-item>
        <n-form-item label="拜师理由">
          <n-input
            v-model:value="applyForm.applyReason"
            type="textarea"
            :rows="5"
            placeholder="请说明你的学习基础、兴趣方向和拜师原因"
          />
        </n-form-item>
      </n-form>
      <template #footer>
        <div style="display:flex; justify-content:flex-end; gap:12px;">
          <n-button @click="applyVisible = false">取消</n-button>
          <n-button type="primary" :loading="applying" @click="submitApply">提交申请</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<style scoped>
.profile-header {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  margin-bottom: 40px;
  padding: 32px;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
}

.profile-header-info h1 {
  font-family: var(--font-serif);
  font-size: 24px;
  margin-bottom: 4px;
}

.profile-title {
  color: var(--accent);
  font-size: 14px;
  margin-bottom: 8px;
}

.profile-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.profile-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-section {
  padding: 24px;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
}

.profile-section h3 {
  font-family: var(--font-serif);
  font-size: 16px;
  margin-bottom: 12px;
  color: var(--text-primary);
}

.profile-section p {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-body);
}
</style>
