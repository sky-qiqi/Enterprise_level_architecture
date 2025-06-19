<template>
  <el-header class="header">
    <div class="icon-div" @click="changeCollapse">
      <i class="icon" :class="collapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
    </div>
    <div class="header-right">
      <div class="search-input"></div>
      <div class="icon-div" @click="full">
        <i class="icon el-icon-full-screen"></i>
      </div>
      <div class="manager-header-right" style="margin-top: 20px">
        <el-dropdown style="cursor: pointer">
          <div style="padding-right: 20px; display: flex; align-items: center">
            <img style="width: 45px; height: 45px; border-radius: 50%;" :src="host + user.avatar" alt="">
            <span style="margin-left: 5px;">{{ user.userName }}</span><el-icon color="#fff"><arrow-down /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleClickUser">个人信息</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
<!--              <el-dropdown-item @click="loginTofront">返回前台</el-dropdown-item>-->
              <el-dropdown-item @click="toPage('index')" :class="$route.path === '/index'?'menu_item':''">返回前台</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script>
  import { removeToken } from '@/utils/token'
  import { mapState, mapGetters } from 'vuex'
  import { logout, getUser } from '@/api/manage.js'

  export default {
    name: "header",
    data() {
      return {
        user: {
          avatar: "",
          userName: ""
        },
        search: "",
        collapse: false,
        host: ""
      }
    },
    computed: {
      ...mapGetters('user', ['getUser'])
    },
    methods: {
      toPage(name) {
        if (name === "index") {
          window.open("http://localhost:3000")
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
      changeCollapse() {
        this.collapse = !this.collapse
        this.$bus.$emit('collapse', this.collapse)
      },
      full() {
        let de = document.documentElement
        if (de.requestFullscreen) {
          de.requestFullscreen()
        } else if (de.mozRequestFullScreen) {
          de.mozRequestFullScreen()
        } else if (de.webkitRequestFullScreen) {
          de.webkitRequestFullScreen()
        }
      },
      getUser() {
        this.$api.getUser().then((res) => {
          if (res.code === 1000) {
            this.user.avatar = res.data.avatar;
          } else {
            this.$message.error(res.message);
          }
        }).catch((err) => {
          console.error(err);
          this.$message.error("获取用户信息失败");
        });
      },
    },
    created() {
      if (this.$store.state.configure && this.$store.state.configure.HOST) {
        this.host = this.$store.state.configure.HOST;
      }
    },
    mounted() {
      this.user = JSON.parse(this.getUser)
    },
  }
</script>

<style scoped>
    .header {
      background-color: #ffffff;
      width: 100%;
      height: 50px !important;
      display: flex;
      flex-direction: row;
      align-items: center;
      padding-left: 0;
      justify-content: space-between;
    }
    .icon-div {
      width: 50px;
      height: 50px;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
    }
    .icon {
      font-size: 20px;
    }
    .header-right {
      width: 43%;
      display: flex;
      flex-direction: row;
      align-items: center;
    }
    .search-input {
      width: 60%;
    }
    .el-input {
      background-color: #E0E0EA !important;
    }


</style>
