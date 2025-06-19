<template>
  <div class="header">
    <div class="header1">
      <img style="height:100%" src="../assets/imgs/bg-2.jpg">
      <div class="header4">
        <div class="menu-list">
          <div @click="toPage('index')" :class="$route.path === '/index'?'menu_item':''">系统主页</div>
          <div @click="toPage('attractions')" :class="$route.path === '/attractions'?'menu_item':''">景点信息</div>
          <div @click="toPage('line')" :class="$route.path === '/line'?'menu_item':''">旅游线路</div>
          <div @click="toPage('forum')" :class="$route.path === '/forum'?'menu_item':''">旅游资讯</div>
          <div @click="toPage('hotel')" :class="$route.path === '/hotel'?'menu_item':''">酒店信息</div>
<!--          <div @click="toPage('rank')" :class="$route.path === '/rank'?'menu_item':''">排行榜</div>-->
        </div>
      </div>
      <div class="manager-header-right" style="margin-top: 20px">
        <el-dropdown style="cursor: pointer">
          <div style="padding-right: 20px; display: flex; align-items: center">
            <img style="width: 45px; height: 45px; border-radius: 50%;"  :src="$store.state.HOST + user.avatar" alt="">
            <span style="margin-left: 5px;color: red">{{ user.userName }}</span><el-icon color="#000000"><arrow-down /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleClickUser">个人信息</el-dropdown-item>
              <el-dropdown-item @click="toPage('favor')" :class="$route.path === '/favor'?'menu_item':''">我的收藏</el-dropdown-item>
              <el-dropdown-item @click="toPage('order')" :class="$route.path === '/order'?'menu_item':''">我的预定</el-dropdown-item>
              <el-dropdown-item divided></el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              <el-dropdown-item @click="toPage('manage')" :class="$route.path === '/manage'?'menu_item':''">登录后台</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script>
import {removeToken} from "@/utils/token.js";
import {getUser, logout} from "@/api/front.js";
import $store from "@/store/configure.js";

export default {
  computed: {
    $store() {
      return $store
    }
  },
  data() {
    return{
      name: "",
      user: {
        avatar: "",
        userName: ""
      },
      avatar: []
    }
  },
  methods: {
    toCenter() {
      this.$router.push("/center")
    },
    toPage(name) {
      if (name === "index") {
        this.$router.push("/index")
      } else if (name === "attractions") {
        this.$router.push("/attractions")
      } else if (name === "line") {
        this.$router.push("/line")
      } else if (name === "hotel") {
        this.$router.push("/hotel")
      } else if (name === "forum") {
        this.$router.push("/forum")
      } else if (name === "rank") {
        this.$router.push("/rank")
      } else if (name === "manage") {
        window.open("http://localhost:3001")
      } else if (name === "order") {
        this.$router.push("/order")
      } else if (name === "favor") {
        this.$router.push("/favor")
      } else if (name === "order") {
        this.$router.push("/order")
      } else if (name === "ai") {
        this.$router.push("/ai")
      }
    },
    logout() {
      // 调用 API 退出登录
      logout().then((res) => {
        if (res.code === 1000) {
          // 清除本地存储的 token
          removeToken();
          // 清除 Vuex 中的用户信息
          this.$store.dispatch('user/logout');
          // 跳转到登录页面
          this.$router.push('/login');
        } else {
          this.$message.error(res.message);
        }
      }).catch((err) => {
        console.error(err);
        this.$message.error("退出登录失败");
      });
    },
    handleClickUser() {
      console.log('点击了个人信息');
      this.$router.push('/center');
    },
    getUserInfo() {
      getUser().then(res => {
        if(res.code === 1000) {
          this.user = res.data
          this.user.sex = res.data.sex + ""
          this.avatar[0] = this.$store.state.configure.HOST + this.user.avatar
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
  },
  mounted() {
    this.getUserInfo()
  }
}
</script>

<style scoped>
.header {
  width: 100%;
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.header1 {
  width: 80%;
  height: 80px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ffffff;
}
.header4 {
  width: 80%;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.menu-list {
  display: flex;
  color: #000000;
}
.menu-list>div{
  font-size: 18px;
  width: 100px;
  cursor: pointer;
}
.menu_item {
  color: #DC1616;
}
.header5 {
  display: flex;
  align-items: center;
}
.header6 {
  margin-left: 10px;
  width: 80px;
  height: 30px;
  border-radius: 20px;
  color: #ffffff;
  background-color: #4381FF;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 13px;
}
</style>
