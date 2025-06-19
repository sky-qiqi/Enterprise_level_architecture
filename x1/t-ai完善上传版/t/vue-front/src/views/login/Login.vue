<template>
  <div class="login">
    <div class="login4">
      <div class="login5">
        <img src="../../assets/imgs/bg-2.jpg">
        <div class="login6">欢迎使用</div>
        <el-input prefix-icon="el-icon-user" v-model="loginAccount" placeholder="请输入登录账号"></el-input>
        <el-input prefix-icon="el-icon-star-off" type="password" v-model="password" placeholder="请输入用户密码"></el-input>
        <div class="login-links">
          <div class="login7" @click="toForget">忘记密码？</div>
          <div class="login-register" @click="toRegister">注册</div>
        </div>
        <div class="login-submit" @click="login">登 录</div>
      </div>
    </div>
  </div>
</template>

<script>
import {login,getUser} from '@/api/front.js'
export default {
  data() {
    return{
      loginAccount: "",
      password: "",
    }
  },
  methods: {
    toRegister() {
      this.$router.push("/register")
    },
    toForget() {
      this.$router.push("/forget")
    },
    login() {
      if (!this.loginAccount) {
        this.$message({
          message: '请输入用户名',
          type: 'warning'
        });
        return;
      }
      if (!this.password) {
        this.$message({
          message: '请输入密码',
          type: 'warning'
        });
        return;
      }
      const params = {
        loginAccount: this.loginAccount,
        password: this.password
      };
      login(params).then(res => {
        if (res.code === 1000) {
          this.$message({
            message: '登录成功',
            type: 'success'
          });
          const that = this;
          const token = res.data.token;
          window.localStorage.setItem("user_token", token)
          this.getUserInfo()
          setTimeout(function () {
            that.$router.push("/index")
          }, 500)
        } else {
          this.$message.error(res.message);
        }
      })
    },
    getUserInfo() {
      getUser().then(res => {
        if (res.code === 1000) {
          window.localStorage.setItem("user_info", JSON.stringify(res.data))
        }
      })
    },
  }
}
</script>

<style scoped>
@import url('@/assets/css/login.css');
</style>
