<template>
  <div class="index">
    <ai></ai>
    <headers></headers>
    <div class="index12">
      <el-carousel height="500px">
        <el-carousel-item v-for="(item,index) in rotations" :key="index">
          <img style="width:100%;height:100%;object-fit:cover;" :src="$store.state.HOST + item.image">
        </el-carousel-item>
      </el-carousel>
    </div>
    <div class="index1">
      <div class="index2">
        <div class="index3">
          推荐景点
        </div>
        <div class="index4">
          <div class="index5" v-for="(item,index) in attractions" :key="index" >
            <img style="width:100%;height:220px" :src="item.images.split(',')[0]">
            <div class="index6">
              <div class="index7">{{item.name}}</div>
              <div class="index8">{{item.introduce}}</div>
            </div>
            <div class="index9" style="margin-top:10px" @click="toInfo(item.id)">
                预 约
              </div>
          </div>
        </div>
        <el-button style="margin-top:10px" size="small" type="primary" plain @click="toAttraction">查看更多</el-button>
      </div>
    </div>
    <div class="index12">
      <rank></rank>
    </div>
  </div>
</template>

<script>
  import {getRotationsList,getAttractionsIndex,getUserCount,getLineIndex} from '@/api/front.js'
  import headers from '@/components/header.vue'
  import ai from '@/components/ai.vue'
  import $store from "@/store/configure.js";
  import rank from '@/views/rank/rank.vue'
  export default {
    computed: {
      $store() {
        return $store
      }
    },
    data() {
      return{
        rotations: [],
        attractions: [],
        count: 0,
        line: [],
      }
    },
    components: {
      headers,
      ai,
      rank
    },
    methods: {
      toAttraction() {
        this.$router.push("/attractions")
      },
      toLine() {
        this.$router.push("/line")
      },
      toInfo(id) {
        this.$router.push("/attractionsInfo?id=" + id)
      },
      toLineInfo(id) {
        this.$router.push("/lineInfo?id=" + id)
      },
      getRotationsList() {
        getRotationsList().then(res => {
          if (res.code === 1000) {
            this.rotations = res.data
          }
        })
      },
      getAttractionsIndex() {
        getAttractionsIndex().then(res => {
          if (res.code === 1000) {
            this.attractions = res.data
          }
        })
      },
      getUserCount() {
        getUserCount().then(res => {
          if (res.code === 1000) {
            this.count = res.data
          }
        })
      },
      getLineIndex() {
        getLineIndex().then(res => {
          if (res.code === 1000) {
            this.line = res.data
          }
        })
      }
    },
    created() {

    },
    mounted() {
      this.getRotationsList()
      this.getAttractionsIndex()
      this.getUserCount()
      this.getLineIndex()
    }
 }
</script>

<style scoped>
@import url('../../assets/css/index.css');
</style>
