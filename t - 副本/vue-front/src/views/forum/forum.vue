<template>
  <div class="forum">
    <ai></ai>
    <headers></headers>
    <div class="attractions1">
      <div class="attractions2">
        <el-input size="small" style="width:300px;margin-left:20px" v-model="search.title" placeholder="请输入资讯"></el-input>
        <el-button size="small" style="margin-left:20px" type="primary" plain @click="searchPage">搜索</el-button>
      </div>
      <div class="forum1">
        <div class="forum2" v-for="(item,index) in tableData" @click="toInfo(item.id)">
            {{index + 1}}.{{item.title}}
        </div>
      </div>
      <el-pagination
        background
        :page-size="search.pageSize"
        layout="prev, pager, next"
        @current-change="handleCurrentChange"
        :total="total"
        style="margin-top:20px;text-align:center"
      >
      </el-pagination>
    </div>
    <bottoms></bottoms>
  </div>
</template>

<script>
  import {getForumPage} from '@/api/front.js'
  import headers from '@/components/header.vue'
  import bottoms from '@/components/bottom.vue'
  import ai from '@/components/ai.vue'
  export default {
    data() {
      return{
        search: {
          title: "",
          pageSize: 12,
          pageNumber: 1,
        },
        total:100,
        tableData: [],
      }
    },
    components: {
      headers,
      bottoms,
      ai
    },
    methods: {
      searchPage() {
        this.pageNumber = 1
        this.getForumPage()
      },
      getForumPage() {
        getForumPage(this.search).then(res => {
          if (res.code === 1000) {
            this.tableData = res.data.records
            this.total = res.data.total
          }
        })
      },
      toInfo(id) {
        this.$router.push("/forumInfo?id=" + id)
      },
      handleCurrentChange(val) {
        this.search.pageNumber = val
        this.getForumPage()
      },
    },
    created() {

    },
    mounted() {
      this.getForumPage()
    }
 }
</script>

<style scoped>
   @import url('../../assets/css/forum.css');
</style>
