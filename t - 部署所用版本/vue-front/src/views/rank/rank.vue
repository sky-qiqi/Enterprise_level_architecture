<template>
  <div class="rank-page">
    <h2>排行榜</h2>
    <el-tabs v-model="activeTab" @tab-click="fetchRankList">
      <el-tab-pane label="景点排行榜" name="attractions"></el-tab-pane>
      <el-tab-pane label="路线排行榜" name="routes"></el-tab-pane>
    </el-tabs>
    <el-table v-if="!loading && rankList.length" :data="rankList" style="width: 100%; margin-top: 20px;" @row-click="goToDetail">
      <el-table-column prop="rank" label="排名" width="80" />
      <el-table-column prop="name" :label="activeTab==='attractions' ? '景点名称' : '路线名称'" />
      <el-table-column
        prop="favorCount"
        label="收藏量"
        width="120"
      >
        <template #default="scope">
          <span :style="{color: userFavorIds.includes(scope.row.id) ? 'red' : ''}">
            {{ scope.row.favorCount }}
            <span v-if="userFavorIds.includes(scope.row.id)">（已收藏）</span>
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="orderCount" label="预定量" width="120" />
    </el-table>
    <div v-if="!loading && !rankList.length" class="empty-tip">暂无数据</div>
    <el-skeleton v-if="loading" :rows="5" animated style="margin-top: 20px;" />

    <div class="guess-section">
      <h3>猜你喜欢</h3>
      <el-row :gutter="16">
        <el-col v-for="item in recommendList" :key="item.id" :span="8">
          <el-card class="guess-card" shadow="hover" @click="goToDetail(item)" style="cursor:pointer;">
            <img :src="parseImage(item.images)" style="width:100%;height:100px;object-fit:cover;">
            <div class="guess-title">{{ item.name }}</div>
            <div class="guess-desc">{{ item.desc || item.description || item.intro || item.introduce || '暂无简介' }}</div>
          </el-card>
        </el-col>
      </el-row>
      <div v-if="recommendLoading" class="guess-loading">
        <el-skeleton :rows="2" animated />
      </div>
      <div v-if="!recommendLoading && !recommendList.length" class="empty-tip">暂无推荐</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavorPage, getAttractionOrderPage, getRecommendRoutes, getRankRoutes } from '@/api/front.js'

const rankList = ref([])
const loading = ref(false)
const recommendList = ref([])
const recommendLoading = ref(false)
const router = useRouter()
const activeTab = ref('attractions')
const userFavorIds = ref([])

// 统计排行榜
const fetchRankList = async () => {
  loading.value = true
  try {
    const res = await getRankRoutes()
    console.log('rankRes', res)
    // 兼容后端返回的字段
    let arr = Array.isArray(res.data) ? res.data : []
    arr.forEach((item, idx) => (item.rank = idx + 1))
    rankList.value = arr
  } catch (e) {
    console.error('fetchRankList error:', e)
    rankList.value = []
  } finally {
    loading.value = false
  }
}

// 推荐区
const fetchRecommend = async () => {
  recommendLoading.value = true
  try {
    const res = await getRecommendRoutes()
    console.log('recommendList', res.data)
    recommendList.value = res.data || []
  } catch (e) {
    recommendList.value = []
  } finally {
    recommendLoading.value = false
  }
}

const goToDetail = (item) => {
  router.push(`/lineInfo?id=${item.id}`)
}

onMounted(() => {
  fetchRankList()
  fetchRecommend()
  fetchUserFavor()
})

const fetchUserFavor = async () => {
  // 只查当前用户的收藏
  const res = await getFavorPage({ pageNum: 1, pageSize: 100 })
  if (res.code === 1000 && Array.isArray(res.data?.list)) {
    userFavorIds.value = res.data.list.map(item => item.line_id)
  }
}

// 兼容图片字段（数组、逗号分隔、字符串）
function parseImage(images) {
  if (!images) return ''
  if (Array.isArray(images)) return images[0]
  if (typeof images === 'string' && images.includes(',')) return images.split(',')[0]
  return images
}
</script>

<style scoped>
.rank-page {
  max-width: 100%;
  margin: 40px auto;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.empty-tip {
  text-align: center;
  color: #888;
  margin-top: 32px;
  font-size: 16px;
}
.guess-section {
  margin-top: 48px;
}
.guess-card {
  margin-bottom: 16px;
  min-height: 200px;
}
.guess-title {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
}
.guess-desc {
  color: #666;
  font-size: 14px;
  min-height: 40px;
  white-space: pre-line;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
}
.guess-loading {
  margin-top: 16px;
}
</style>
