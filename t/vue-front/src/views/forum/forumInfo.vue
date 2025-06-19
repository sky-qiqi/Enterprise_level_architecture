<template>
  <div class="forumInfo">
    <headers></headers>
    <el-card class="forumInfo1">
      <div class="forumInfo2">
        <div class="forumInfo3">
          <el-text style="font-size: 24px">{{ info.title }}</el-text>
        </div>
        <div class="forumInfo4" v-html="info.content"></div>
      </div>
    </el-card>
    <bottoms></bottoms>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getForumById } from '../../api/front.js';
import headers from '@/components/header.vue';
import bottoms from '@/components/bottom.vue';

const route = useRoute();
const id = ref('');
const info = ref({});

const fetchForumById = () => {
  getForumById({ id: id.value }).then((res) => {
    if (res.code === 1000) {
      info.value = res.data;
    }
  });
};

onMounted(() => {
  id.value = route.query.id;
  fetchForumById();
});
</script>

<style scoped>
@import url('../../assets/css/forumInfo.css');
::v-deep img {
  max-width: 100% !important;
}
</style>
