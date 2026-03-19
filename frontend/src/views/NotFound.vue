<template>
  <div class="container" style="text-align:center; padding: 120px 24px;">
    <h1 style="font-size:72px; font-family:var(--font-serif); color:var(--text-secondary); margin-bottom:16px;">404</h1>
    <p style="font-size:16px; color:var(--text-secondary); margin-bottom:32px;">页面未找到</p>
    <router-link to="/">
      <n-button type="primary">返回首页</n-button>
    </router-link>
  </div>
</template>
<script setup lang="ts">
import { NButton } from 'naive-ui'
</script>
