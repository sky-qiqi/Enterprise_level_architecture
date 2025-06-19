<template>
  <div class="login">
    <div class="login1">
    </div>
    <div class="login4">
      <div class="login5">
        <img src="../../assets/imgs/bg-2.jpg" style="margin-bottom: 10px;">
        <el-input  v-model="loginAccount" placeholder="请输入登录账号">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
        <el-input type="password" v-model="password" placeholder="请输入用户密码">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
        <el-input v-model="userName" placeholder="请输入用户名">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
        <el-input v-model="email" placeholder="请输入邮箱">
          <template #prefix>
            <el-icon><Message /></el-icon>
          </template>
        </el-input>
        <el-input  v-model="tel" placeholder="请输入联系电话">
          <template #prefix>
            <el-icon><Iphone /></el-icon>
          </template>
        </el-input>
        <div class="login8-container">
          <div class="login8" @click="saveUser">
            点击注册
          </div>
          <div class="login8" @click="toLogin">
            已有账号去登录
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import {saveUser} from '@/api/front.js'
  export default {
    data() {
      return{
        loginAccount: "",
        password: "",
        userName: "",
        type: "1",
        email: "",
        tel: "",
      }
    },
    methods: {
        toLogin() {
            this.$router.push("/logIn")
        },
        saveUser() {
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
            if(!this.userName) {
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
            if(!this.tel) {
                this.$message({
                    message: '请输入联系方式',
                    type: 'warning'
                });
                return;
            }
          const param = {
            loginAccount: this.loginAccount,
            password: this.password,
            userName: this.userName,
            email: this.email,
            tel: this.tel,
            userType: 1,
          };
          saveUser(param).then(res => {
                if (res.code === 1000) {
                    if (this.type === 2) {
                        this.$message({
                            message: '正在审核，审核完成后会将结果发送到您的邮箱，请注意查收',
                            type: 'success'
                        });
                    } else {
                        this.$message({
                            message: '注册成功',
                            type: 'success'
                        });
                    }
                  const that = this;
                  setTimeout(function() {
                        that.$router.push("/login")
                    },500)
                } else {
                    this.$message({
                        message: res.message,
                        type: 'warning'
                    });
                }
            })
        }
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
