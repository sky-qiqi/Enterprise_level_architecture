<template>
  <div class="login">
    <div class="login4">
      <div class="login5">
        <img src="../../assets/imgs/bg-2.jpg" style="margin-bottom: 10px;">
        <div class="login6">欢迎使用</div>
        <el-input v-model="loginAccount" placeholder="请输入登录账号">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
        <el-input type="password" v-model="password" placeholder="请输入用户密码">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
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
          
          // 添加调试信息
          console.log('登录响应数据:', res);
          console.log('保存的 token:', localStorage.getItem('user_token'));
          console.log('当前路由:', this.$router.currentRoute.value);
          
          // 获取用户信息
          this.getUserInfo().then(() => {
            console.log('用户信息获取成功');
            // 延迟跳转，给足够时间查看控制台
            setTimeout(function () {
              console.log('准备跳转到首页');
              that.$router.push("/index").catch(err => {
                console.error('路由跳转失败:', err);
              });
            }, 10000); // 延长到10秒
          }).catch(err => {
            console.error('获取用户信息失败:', err);
          });
        } else {
          this.$message.error(res.message);
        }
      }).catch(err => {
        console.error('登录请求失败:', err);
        this.$message.error('登录失败，请重试');
      });
    },
    getUserInfo() {
      return getUser().then(res => {
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
