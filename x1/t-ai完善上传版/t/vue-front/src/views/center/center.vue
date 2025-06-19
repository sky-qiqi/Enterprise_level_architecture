<template>
  <div class="centerPage">
    <headerPage></headerPage>
    <div class="centerPage-content">
      <el-card class="box-card">
        <div class="content">
          <div class="master">
            <el-form
                style="margin-right: 20px"
                :model="user"
                :rules="rules"
                ref="ruleForm"
                label-width="140px"
            >
              <el-form-item label="登录账号" prop="loginAccount">
                <el-input size="small" disabled v-model="user.loginAccount"></el-input>
              </el-form-item>
              <el-form-item label="用户名" prop="userName">
                <el-input size="small" v-model="user.userName"></el-input>
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input size="small" v-model="user.email"></el-input>
              </el-form-item>
              <el-form-item label="联系电话" prop="tel">
                <el-input size="small" v-model="user.tel"></el-input>
              </el-form-item>
              <el-form-item label="性别" prop="sex">
                <el-radio-group v-model="user.sex">
                  <el-radio label="0">男</el-radio>
                  <el-radio label="1">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="上次修改密码时间" prop="pwdUpdateDate">
                <el-input size="small" disabled v-model="user.pwdUpdateDate"></el-input>
              </el-form-item>
            </el-form>
            <div class="submit">
              <el-button type="primary" plain size="small" @click="submit">保存</el-button>
            </div>
          </div>
          <div class="slave">
            <div class="img">
              <el-image
                  style="object-fit: cover; width: 300px; height: 300px; overflow: hidden; border-radius: 50%"
                  :src="$store.state.HOST + user.avatar"
                  :preview-src-list="avatar"
              />
            </div>
            <div class="btns">
              <div>
                <el-upload
                    ref="upload"
                    :action="uploadAvatarUrl()+ '/'+ this.user.id"
                    :show-file-list="false"
                    :before-upload="beforeAvatorUpload"
                    :on-success="handleAvatorSuccess"
                    accept="image/*"
                >
                  <el-button size="mini">修改头像</el-button>
                </el-upload>
              </div>
              <div style="margin-top: 15px">
                <el-button size="mini" @click="changePassword">修改密码</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <el-dialog
        title="修改密码"
        v-model="passwordDialogVisible"
        width="30%"
        @close="handlePasswordClose"
    >
      <span>请输入{{ user.userName }}的旧密码：</span>
      <el-input
          style="margin-top: 10px"
          show-password
          v-model="oldPassword"
          size="small"
          autocomplete="off"
      ></el-input>
      <span>请输入{{ user.userName }}的新密码：</span>
      <el-input
          style="margin-top: 10px"
          show-password
          v-model="newPassword"
          size="small"
          autocomplete="off"
      ></el-input>
      <template #footer>
        <el-button size="small" @click="handlePasswordClose">取消</el-button>
        <el-button size="small" type="primary" @click="passwordSubmit">确定</el-button>
      </template>
    </el-dialog>

    <bottomPage></bottomPage>
  </div>
</template>

<script>
import headerPage from "../../components/header.vue";
import bottomPage from "../../components/bottom.vue";
import {mixin} from "../../minix";
import {getUser, setUserInfo, setUserAvatar, changePassword} from '@/api/front.js'
export default {
  components: {
    headerPage,
    bottomPage,
  },
  mixins: [mixin],
  data() {
    const checkPhone = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入联系电话'));
      } else {
        const reg = /^1[3|4|5|7|8][0-9]\d{8}$/
        if (reg.test(value)) {
          callback();
        } else {
          return callback(new Error('请输入正确的联系电话'));
        }
      }
    };
    return {
      user: {
        avatar: "", // 初始为空，等待接口返回
      },
      avatar: [],
      passwordDialogVisible: false, // 控制密码对话框显示
      oldPassword: '', // 旧密码绑定
      newPassword: '', // 新密码绑定
      rules: {
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        tel: [
          { required: true, validator: checkPhone, message: '请输入正确的联系电话', trigger: 'blur' },
        ],
      },
    };
  },
  methods: {
    submit() {
      this.$refs["ruleForm"].validate((valid) => {
        if (valid) {
          setUserInfo(this.user).then(res => {
            if(res.code === 1000) {
              this.$message({
                type: 'success',
                message: '保存成功!'
              });
              this.getUserInfo()
            } else {
              this.$notify.error({
                title: '错误',
                message: res.message
              });
            }
          })
        } else {
          return false;
        }
      });
    },
    getUserInfo() {
      getUser().then(res => {
        if(res.code === 1000) {
          this.user = res.data
          this.user.sex = res.data.sex + ""
          this.avatar[0] = this.$store.state.configure.HOST + this.user.avatar
          // this.setUser()
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    setUser() {
      this.$store.commit('user/setUser', JSON.stringify(this.user))
    },
    handleAvatorSuccess(res){
      let _this = this;
      if(res.code === 1000){
        _this.$message({
          type: 'success',
          message: '上传成功!'
        });
        this.getUserInfo()
      }else{
        _this.$notify.error({
          title: '错误',
          message: res.message
        });
      }
    },
    changePassword() {
      this.passwordDialogVisible = true;
    },
    handlePasswordClose() {
      this.passwordDialogVisible = false;
    },
    passwordSubmit() {
      const param = {
        id: this.user.id,
        password: this.oldPassword,
        newPassword: this.newPassword,
      };
      changePassword(param).then((res) => {
        if (res.code === 1000) {
          this.$message.success("密码修改成功");
          this.passwordDialogVisible = false;

          // 清除用户信息
          this.$store.commit('user/setUser', null);

          // 跳转到登录页面
          this.$router.push('/login');
        } else {
          this.$message.error(res.message);
        }
      });
    },
  },

  created() {

  },
  mounted() {
    this.getUserInfo()
  }
}
</script>

<style scoped>
.centerPage {
  width: 100%;
  height: 100%;
}

.centerPage-content {
  width: 100%;
  display: flex;
  justify-content: center;
}

.box-card {
  margin-top: 30px;
  margin-bottom: 30px;
  width: 70%;
}

.content {
  width: 100%;
  height: 550px;
  display: flex;
  flex-direction: row;
}

.master {
  width: 60%;
  border-right: 1px solid #e5e5e5;
}

.submit {
  width: 100%;
  text-align: center;
}

.slave {
  width: 40%;
}

.img {
  width: 100%;
  height: 75%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.btns {
  width: 100%;
  height: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
