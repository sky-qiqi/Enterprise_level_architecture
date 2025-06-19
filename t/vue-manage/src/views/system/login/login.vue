<template>
  <div class="login">
    <div class="login4">
      <div class="login5">
        <img src="../../../assets/imgs/bg-2.jpg" style="width: 100%; height: auto;">
        <div class="login6">登录旅游信息推荐系统后台</div>
        <el-input v-model="loginAccount" placeholder="请输入登录账号" style="margin-bottom:10px ">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
        <el-input type="password" v-model="password" placeholder="请输入用户密码">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
        <div class="login8" @click="login">
          登 录
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login, getUser } from '@/api/manage.js'
export default {
  data() {
    return {
      loginAccount: '',
      password: ''
    }
  },
  methods: {
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
      var params = {
        loginAccount: this.loginAccount,
        password: this.password
      }
      login(params).then(res => {
        if (res.code === 1000) {
          this.$message({
            message: '登录成功',
            type: 'success'
          });
          var that = this;
          var token = res.data.token;
          this.$store.commit('user/setToken', token);
          this.getUserInfo();
          setTimeout(function () {
            that.$router.push("/index");
          }, 500);
        } else {
          this.$message.error(res.message);
        }
      })
    },
    getUserInfo() {
      getUser().then(res => {
        if (res.code === 1000) {
          this.$store.commit('user/setUser', JSON.stringify(res.data));
        }
      })
    },
  },
  created() {

  },
  mounted() {

  }
}
</script>

<style scoped>
.login {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center; /* 修改为水平居中 */
  align-items: center; /* 垂直居中 */
  background-image: url('@/assets/imgs/bg-1.jpg');
  background-size: cover;
  background-position: center;
}


.login4 {
  width: 400px;
  background: rgba(255, 255, 255, 0.9);
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 0 20px rgba(0,0,0,0.2);
}


.login5 {
  width: 100%;
  display: flex;
  flex-direction: column;
}
.login6 {
  font-size: 24px;
  text-align: center;
  margin-bottom: 15px;
}
.login8 {
  width: 96%;
  background-color: #409eff;
  color: white;
  padding: 8px;
  border-radius: 4px;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 15px;
}
</style>
