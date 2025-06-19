<template>
  <div class="forumInfo">
    <ai></ai>
    <headers></headers>
    <div class="forumInfo1">
      <div class="forumInfo2">
        <el-carousel height="500px" v-if="info.images && info.images.length > 0">
          <el-carousel-item v-for="(item,index) in info.images.split(',')" :key="index">
            <img v-if="item" style="width:100%;height:100%" :src="item">
            <div v-else style="width:100%;height:100%; background-color: #e9e9e9; text-align: center; line-height: 500px;">
              无效图片链接
            </div>
          </el-carousel-item>
        </el-carousel>
        <div v-else-if="!info.images && id">
          正在加载图片...
        </div>
        <div v-else>
          暂无图片
        </div>
      </div>
    </div>
    <div class="forumInfo1">
      <div class="forumInfo2">
        <div class="forumInfo3">
          <h1>{{ info.name }}</h1>
          <el-button v-if="flag === false" size="large" type="success" circle @click="saveFavor">
            <el-icon><Star /></el-icon>
          </el-button>
          <el-button v-if="flag === true" size="large" type="warning" circle @click="removeFavor">
            <el-icon>
              <el-icon><StarFilled /></el-icon>
            </el-icon>
          </el-button>
        </div>
        <div class="forumInfo4" v-html="info.content"></div>
      </div>
    </div>
  </div>
</template>

<script>
import {getLineById,getFavor,saveFavor,removeFavor} from '@/api/front.js'
import headers from '@/components/header.vue'
import ai from '@/components/ai.vue'

export default {
  data() {
    return{
      id: "",
      info: {
        // 确保初始化时有 images 属性，可以设为 null 或空字符串
        content: "",
        name: "",
        images: null // 或者 ''
      },
      favor: {},
      flag: null, // 初始化为 null 或 undefined，表示未知状态
    }
  },
  components: {
    headers,
    ai
  },
  methods: {
    removeFavor() {
      // 确保 favor.id 存在再调用
      if (!this.favor || !this.favor.id) {
        this.$message.warning('收藏信息异常，无法取消收藏');
        return;
      }
      removeFavor({ids: this.favor.id}).then(res => {
        if (res.code === 1000) {
          this.$message({
            message: '取消收藏成功',
            type: 'success'
          });
          this.getFavor() // 取消成功后刷新收藏状态
        } else {
          // 处理取消失败的情况
          this.$message.error(res.msg || '取消收藏失败');
        }
      }).catch(error => {
        // 处理 API 请求错误
        this.$message.error('取消收藏请求出错');
        console.error("Remove favor API error:", error);
      });
    },
    saveFavor() {
      // 确保 id 存在再调用
      if (!this.id) {
        this.$message.warning('缺少商品ID，无法收藏');
        return;
      }
      var param = {
        lineId: this.id
      }
      saveFavor(param).then(res => {
        if (res.code === 1000) {
          this.$message({
            message: '收藏成功',
            type: 'success'
          });
          this.getFavor() // 收藏成功后刷新收藏状态
        } else {
          // 处理收藏失败的情况
          this.$message.error(res.msg || '收藏失败');
        }
      }).catch(error => {
        // 处理 API 请求错误
        this.$message.error('收藏请求出错');
        console.error("Save favor API error:", error);
      });
    },
    getLineById() {
      // 确保 id 存在再调用
      if (!this.id) {
        console.warn("ID is not set for getLineById");
        return;
      }
      getLineById({id: this.id}).then(res => {
        if (res.code == 1000 && res.data) { // 检查 res.data 是否存在
          this.info = res.data;
          // 可以在这里进一步检查 images 是否是字符串，如果不是，则设为 ''
          if (typeof this.info.images !== 'string') {
            this.info.images = '';
          }
        } else {
          // 处理获取详情失败的情况
          console.error("Error fetching line info:", res.msg || 'Unknown error');
          this.$message.error(res.msg || '获取商品详情失败');
          // 可以将 info 重置为初始状态
          this.info = { content: "", name: "", images: '' };
        }
      }).catch(error => {
        // 处理 API 请求错误
        console.error("API request failed for getLineById:", error);
        this.$message.error('获取商品详情请求出错');
        this.info = { content: "", name: "", images: '' }; // 请求失败时重置
      });
    },
    getFavor() {
      // 确保 id 存在再调用
      if (!this.id) {
        console.warn("ID is not set for getFavor");
        this.flag = false; // 没有ID，肯定没有收藏
        return;
      }
      getFavor({id:this.id}).then(res => {
        if (res.code === 1000 && res.data) { // 检查 res.data 是否存在
          this.favor = res.data;
          this.flag = true; // 已收藏
        } else {
          // code 不是 1000 或者没有 data 都认为是未收藏
          this.flag = false; // 未收藏
          this.favor = {}; // 未收藏时清空 favor 数据
          if (res.code !== 1000) {
            console.warn("Get favor API returned code:", res.code, "Msg:", res.msg);
          }
        }
      }).catch(error => {
        // 处理 API 请求错误
        console.error("API request failed for getFavor:", error);
        this.flag = false; // 请求出错也认为是未收藏
        this.favor = {}; // 请求出错时清空 favor 数据
      });
    },
  },
  // created 通常不适合在客户端获取 $route，mounted 更常用且安全
  // created() {},
  mounted() {
    // 在 mounted 中确保获取到 ID
    const id = this.$route.query.id;
    if (id) {
      this.id = id;
      // 确保 ID 获取后再调用获取数据的 API
      this.getFavor();
      this.getLineById();
    } else {
      console.error("Route parameter 'id' is missing!");
      // 如果缺少 ID，可能需要提示用户或重定向
      this.$message.error('缺少商品ID，无法显示详情');
      // 示例：重定向到首页 this.$router.replace('/');
    }
  }
}
</script>

<style scoped>
/* 确保这里的路径正确 */
@import url('../../assets/css/forumInfo.css');
::v-deep img {
  max-width: 100% !important;
}
</style>
