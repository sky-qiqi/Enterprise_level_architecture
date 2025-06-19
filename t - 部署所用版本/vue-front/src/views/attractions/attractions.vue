<template>
  <div class="attractions">
    <ai></ai>
    <headers></headers>
    <div class="attractions1">
      <div class="attractions2">
        <el-input size="small" style="width:300px;margin-left:20px" v-model="search.name" placeholder="请输入景点名称"></el-input>
        <el-button size="small" style="margin-left:20px" type="primary" plain @click="searchPage">搜索</el-button>
      </div>
      <div class="attractions3">
        <div class="index5" v-for="(item, index) in tableData" :key="index" style="margin-top:15px">
          <div class="image-container">
            <img :src="item.images.split(',')[0]" alt="景点图片" class="hotel-image">
          </div>
          <div class="index6">
            <div style="font-size: 24px; font-weight: bold">{{ item.name }}</div>
            <div class="index8">{{ item.introduce }}</div>
            <div class="index9" @click="toInfo(item.id)" style="margin-bottom:10px;cursor: pointer;margin-top:10px">
              查看详情
            </div>
          </div>
        </div>
      </div>
      <el-pagination
          background
          :page-size="search.pageSize"
          layout="prev, pager, next"
          @current-change="handleCurrentChange"
          :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {getAttractionsPage} from '@/api/front.js'
import headers from '@/components/header.vue'
import ai from '@/components/ai.vue'

export default {
  data() {
    return {
      search: {
        name: "",
        state: 1,
        pageSize: 12,
        pageNumber: 1,
      },
      total: 100,
      tableData: [],
    }
  },
  components: {
    headers,
    ai
  },
  methods: {
    searchPage() {
      this.pageNumber = 1
      this.getAttractionsPage()
    },
    getAttractionsPage() {
      getAttractionsPage(this.search).then(res => {
        if (res.code === 1000) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      })
    },
    toInfo(id) {
      this.$router.push("/attractionsInfo?id=" + id)
    },
    handleCurrentChange(val) {
      this.search.pageNumber = val
      this.getAttractionsPage()
    },
  },
  created() {

  },
  mounted() {
    this.getAttractionsPage()
  }
}
</script>

<style scoped>
@import "../../assets/css/attractions.css";
</style>
