<template>
  <div class="login">
    <div class="login1">
    </div>
    <div class="login4">
        <div class="login5">
            <img src="../../assets/imgs/bg-2.jpg" style="margin-bottom: 10px;">
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
            <el-input v-model="email" placeholder="请输入邮箱">
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
            <el-input v-model="code" placeholder="请输入验证码">
              <template #prefix>
                <el-icon><Comment/></el-icon>
              </template>
            </el-input>
            <div class="login8" @click="forgetPassword">
                提交
            </div>
            <div class="login8" @click="getEmailReg">
                获取验证码
            </div>
        </div>
    </div>
  </div>
</template>

<script>
  import {getEmailReg,forgetPassword} from '@/api/front.js'
  export default {
    data() {
      return{
        loginAccount: "",
        password: "",
        email: "",
        code: "",
      }
    },
    methods: {
        toLogin() {
            this.$router.push("/logIn")
        },
        getEmailReg() {
          if(!this.email) {
              this.$message({
                  message: '请输入邮箱',
                  type: 'warning'
              });
              return;
          }
          var regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/; // 邮箱正则表达式
          if (!regex.test(this.email)) {
              this.$message({
                  message: '请输入正确的邮箱格式',
                  type: 'warning'
              });
              return;
          }
          getEmailReg({email: this.email}).then(res => {
            if (res.code == 100) {
              this.$message({
                  message: '验证码已发送',
                  type: 'success'
              });
            } else {
              this.$message({
                  message: res.message,
                  type: 'warning'
              });
            }
          })
        },
        forgetPassword() {
          if(!this.loginAccount) {
              this.$message({
                  message: '请输入登录账号',
                  type: 'warning'
              });
              return;
          }
          if(!this.password) {
              this.$message({
                  message: '请输入密码',
                  type: 'warning'
              });
              return;
          }
          if(!this.code) {
              this.$message({
                  message: '请输入用户名',
                  type: 'warning'
              });
              return;
          }
          if(!this.email) {
              this.$message({
                  message: '请输入邮箱',
                  type: 'warning'
              });
              return;
          }
          var regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/; // 邮箱正则表达式
          if (!regex.test(this.email)) {
              this.$message({
                  message: '请输入正确的邮箱格式',
                  type: 'warning'
              });
              return;
          }
          var param = {
            loginAccount: this.loginAccount,
            password: this.password,
            email: this.email,
            code: this.code
          }
          forgetPassword(param).then(res => {
            if (res.code === 1000) {
              this.$message({
                  message: '密码修改成功',
                  type: 'success'
              });
              var that = this
              setTimeout(function() {
                  that.$router.push("/logIn")
              },500)
            } else {
              this.$message({
                  message: res.message,
                  type: 'warning'
              });
            }
          })
        }
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
  background-image: url('@/assets/imgs/bg-1.jpg');
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
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
  color: #000000;
}

.login-links {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.login7 {
  color: #666;
  cursor: pointer;
  font-size: 14px;
}

.login7:hover {
  color: #409eff;
}

.login8-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.login8 {
  color: #3B96FF;
  cursor: pointer;
  font-size: 14px;
  padding: 5px 10px;
  border-radius: 4px;
  text-align: center;
  transition: background-color 0.3s;
}

.login8:hover {
  background-color: #ecf5ff;
}

.el-input {
  margin-bottom: 15px;
}
</style>
