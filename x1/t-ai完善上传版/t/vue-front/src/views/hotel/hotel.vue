<template>
  <div class="hotel">
    <ai></ai>
    <headers></headers>
    <div class="attractions1">
      <div class="attractions2">
        <el-select size="small" style="margin-left:20px;width:200px" v-model="search.attractions" placeholder="请选择景点">
          <el-option v-for="(item,index) in attractions" :key="index"
                     :label="item.name"
                     :value="item.name">
          </el-option>
        </el-select>
        <el-input size="small" style="width:300px;margin-left:20px" v-model="search.name" placeholder="请输入酒店名称"></el-input>
        <el-button size="small" style="margin-left:20px;margin-right:20px" type="primary" plain @click="searchPage">搜索</el-button>
      </div>
      <div class="attractions3">
        <div class="index5" v-for="(item,index) in tableData" :key="index" style="margin-top:15px">
          <img :src="item.images.split(',')[0]" class="hotel-image">
          <div class="index6">
            <div class="hotel-name" :title="item.name">{{ item.name }}</div>
            <div class="index8" :style="{ maxHeight: '140px', overflowY: 'auto' }">{{ item.introduce }}</div>
            <div class="index9" style="margin-bottom:10px" @click="toInfo(item.id)">
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
          :total="total"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { getHotelPage, getAttractionsList } from '@/api/front.js'
import headers from '@/components/header.vue'
import ai from '@/components/ai.vue'
export default {
  data() {
    return {
      search: {
        name: "",
        state: "1",
        attractions: "",
        pageSize: 12,
        pageNumber: 1,
      },
      total: 100,
      tableData: [],
      attractions: [],
    }
  },
  components: {
    headers,
    ai
  },
  methods: {
    getAttractionsList() {
      getAttractionsList().then(res => {
        if (res.code === 1000) {
          this.attractions = res.data
        }
      })
    },
    searchPage() {
      this.pageNumber = 1
      this.getHotelPage()
    },
    getHotelPage() {
      getHotelPage(this.search).then(res => {
        if (res.code === 1000) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      })
    },
    toInfo(id) {
      this.$router.push("/hotelInfo?id=" + id)
    },
    handleCurrentChange(val) {
      this.search.pageNumber = val
      this.getHotelPage()
    },
  },
  created() {

  },
  mounted() {
    this.getAttractionsList()
    this.getHotelPage()
  }
}
</script>

<style scoped>
@import "@/assets/css/hotel.css";
</style>
