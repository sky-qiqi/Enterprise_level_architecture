<template>
  <div class="user">
    <div class="search-table">
      <div class="search">
        <el-row :gutter="10" style="padding:10px">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">用户名称:</span>
            <el-input
                style="margin-top:10px"
                size="mini"
                placeholder="请输入用户名称"
                v-model="search.userName"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">手机号码:</span>
            <el-input
                style="margin-top:10px"
                size="mini"
                placeholder="请输入手机号码"
                v-model="search.tel"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">状态:</span>
            <el-select
                clearable
                style="margin-top:10px"
                size="mini"
                v-model="search.status"
                placeholder="请选择"
            >
              <el-option label="正常" value="0" />
              <el-option label="停用" value="1" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-button style="margin-top:10px" size="mini" type="primary" @click="searchPage">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button style="margin-top:10px" size="mini"@click="refresh">
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-col>
        </el-row>
      </div>
      <div class="table">
        <el-row style="padding-top:10px;margin-left:10px">
          <el-button type="primary" size="mini" plain @click="add">
            <el-icon><Plus /></el-icon>新增</el-button>
          <el-button type="danger" :disabled="update.length <= 0 ?true:false" size="mini" plain @click="deleteDataBtn">
            <el-icon><Delete /></el-icon>删除
          </el-button>
        </el-row>
        <el-table
            :data="tableData"
            @selection-change="handleSelectionChange"
            stripe
            style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="userName" label="用户名称" width="150" />
          <el-table-column prop="loginAccount" label="登录账号" width="150" />
          <el-table-column prop="tel" label="手机号" width="150" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 0">正常</el-tag>
              <el-tag v-else-if="row.status === 1" type="warning">停用</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" />
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-dropdown trigger="hover" @command="handleCommand">
                <span class="el-dropdown-link">
                  操作<i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item icon="el-icon-edit" :command="`${row.id}#edit`">
                      修改
                    </el-dropdown-item>
                    <el-dropdown-item
                        v-if="row.loginAccount !== 'admin'"
                        icon="el-icon-check"
                        :command="`${row.id}#pass#${row.userName}`"
                    >
                      重置密码
                    </el-dropdown-item>
                    <el-dropdown-item
                        v-if="row.loginAccount !== 'admin'"
                        icon="el-icon-remove"
                        :command="`${row.id}#remove`"
                    >
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
            background
            layout="total, prev, pager, next"
            :page-sizes="[20, 30, 50, 100]"
            :page-size="search.pageSize"
            :current-page="search.pageNumber"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :total="total"
        />
      </div>
    </div>
    <el-dialog
        v-model="passwordDialogVisible"
        title="重置密码"
        width="30%"
        :before-close="handlePasswordClose"
    >
      <span>请输入{{ userName }}的新密码：</span>
      <el-input
          style="margin-top:10px"
          show-password
          v-model="newPassword"
          size="mini"
          autocomplete="off"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button size="mini" @click="handlePasswordClose">取 消</el-button>
          <el-button size="mini" type="primary" @click="passwordSubmit">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增用户弹窗 -->
    <el-dialog title="新增普通用户" width="40%" :destroy-on-close="true" v-model="addUserVisible" :before-close="handleCloseAdd">
      <el-form :model="addForm" :rules="addRules" ref="addRuleForm">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="userName"  label="昵称">
                <el-input v-model="addForm.userName" size="mini" placeholder="请输入用户昵称" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>

          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="tel"  label="号码">
                <el-input v-model="addForm.tel" size="mini" placeholder="请输入手机号码" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="email"  label="邮箱">
                <el-input v-model="addForm.email" size="mini" placeholder="请输入邮箱" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="loginAccount"  label="账号">
                <el-input v-model="addForm.loginAccount" size="mini" placeholder="请输入登录账号" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="password"  label="密码">
                <el-input v-model="addForm.password" show-password size="mini" placeholder="请输入用户密码" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="sex"  label="性别">
                <el-select size="mini" v-model="addForm.sex" placeholder="请选择用户性别">
                  <el-option label="男" value="0"></el-option>
                  <el-option label="女" value="1"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="name"  label="状态">
                <el-radio-group style="margin-top:1px;height:40px;display:flex;align-items: center" v-model="addForm.status">
                  <el-radio label="0">正常</el-radio>
                  <el-radio label="1">停用</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>
<!--          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">-->
<!--            <span class="search-title">&nbsp;&nbsp;&nbsp;&nbsp;备注:</span>-->
<!--            <div style="width:100%">-->
<!--              <el-input type="textarea" v-model="addForm.remark"></el-input>-->
<!--            </div>-->
<!--          </el-col>-->
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" type="primary" @click="submitAdd">确 定</el-button>
        <el-button size="mini" @click="handleCloseAdd">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 修改用户弹窗 -->
    <el-dialog title="修改普通用户" width="40%" :destroy-on-close="true" v-model="updateUserVisible" :before-close="handleCloseUpdate">
      <el-form :model="updateForm" :rules="updateRules" ref="updateRuleForm">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="userName"  label="昵称">
                <el-input v-model="updateForm.userName" size="mini" placeholder="请输入用户昵称" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>

          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="tel"  label="号码">
                <el-input v-model="updateForm.tel" size="mini" placeholder="请输入手机号码" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="email"  label="邮箱">
                <el-input v-model="updateForm.email" size="mini" placeholder="请输入邮箱" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="loginAccount"  label="账号">
                <el-input v-model="updateForm.loginAccount" size="mini" placeholder="请输入登录账号" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="sex"  label="性别">
                <el-select size="mini" v-model="updateForm.sex" placeholder="请选择用户性别">
                  <el-option label="男" value="0"></el-option>
                  <el-option label="女" value="1"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="name" label="状态">
                <el-radio-group style="margin-top:1px;height:40px;display:flex;align-items: center" v-model="updateForm.status">
                  <el-radio label="0">正常</el-radio>
                  <el-radio label="1">停用</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>
<!--          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">-->
<!--            <span class="search-title">&nbsp;&nbsp;&nbsp;&nbsp;备注:</span>-->
<!--            <div style="width:100%">-->
<!--              <el-input type="textarea" v-model="updateForm.remark"></el-input>-->
<!--            </div>-->
<!--          </el-col>-->
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" type="primary" @click="submitUpdate">确 定</el-button>
        <el-button size="mini" @click="handleCloseUpdate">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { getUserPage, removeUser, resetPassword, saveUser, getUserById, editUser } from '@/api/manage.js'

// 响应式数据
const search = reactive({
  userName: '',
  tel: '',
  status: '',
  userType: 1,
  pageNumber: 1,
  pageSize: 10
})
const tableData = ref([])
const total = ref(0)
const addUserVisible = ref(false)
const updateUserVisible = ref(false)
const updateId = ref('')
const passwordDialogVisible = ref(false)
const userName = ref('')
const userId = ref('')
const newPassword = ref('')
const update = ref([])
const remove = ref([])

// 新增用户表单
const addForm = reactive({
  userName: "",
  loginAccount: "",
  userType: 1,
  email: "",
  tel: "",
  sex: "",
  avatar: "",
  password: "",
  status: "0",
  remark: "",
})
const addRules = {
  userName: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
  ],
  loginAccount: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入登录密码', trigger: 'blur' },
  ],
  tel: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'blur' },
  ],
}

// 修改用户表单
const updateForm = reactive({
  userName: "",
  loginAccount: "",
  userType: 1,
  email: "",
  tel: "",
  sex: "",
  avatar: "",
  password: "",
  status: "0",
  remark: "",
})
const updateRules = {
  userName: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
  ],
  loginAccount: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入登录密码', trigger: 'blur' },
  ],
  tel: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'blur' },
  ],
}

// 生命周期钩子
onMounted(() => {
  query()
})

// 方法
const query = async () => {
  try {
    const res = await getUserPage(search)
    if (res.code === 1000) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElNotification.error({
        title: '错误',
        message: res.message
      })
    }
  } catch (error) {
    console.error('查询失败:', error)
  }
}

const refresh = () => {
  search.userName = ''
  search.tel = ''
  search.status = ''
  query()
}

const handleSizeChange = (val) => {
  search.pageSize = val
  query()
}

const handleCurrentChange = (val) => {
  search.pageNumber = val
  query()
}

const handleSelectionChange = (val) => {
  update.value = val.map(item => item.id)
  remove.value = val.map(item => item.id)
}

const addUserFalse = () => {
  addUserVisible.value = false
  query()
}

const updateUserFalse = () => {
  updateUserVisible.value = false
  updateId.value = ''
  query()
}

const handleCommand = (command) => {
  const [id, action, name] = command.split('#')
  switch (action) {
    case 'edit':
      updateId.value = id
      getUserById({ id }).then(res => {
        if (res.code === 1000) {
          updateForm.userName = res.data.userName
          updateForm.loginAccount = res.data.loginAccount
          updateForm.email = res.data.email
          updateForm.tel = res.data.tel
          updateForm.sex = res.data.sex + ""
          updateForm.status = res.data.status + ""
          updateForm.remark = res.data.remark
          updateUserVisible.value = true
        } else {
          ElNotification.error({
            title: '错误',
            message: res.message
          })
        }
      })
      break
    case 'pass':
      userName.value = name
      userId.value = id
      passwordDialogVisible.value = true
      break
    case 'remove':
      handleDelete(id)
      break
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除选中的数据?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteData(id)
  }).catch(() => {})
}

const deleteData = async (ids) => {
  try {
    const res = await removeUser({ ids })
    if (res.code === 1000) {
      ElMessage.success('删除成功!')
      query()
    } else {
      ElNotification.error({
        title: '错误',
        message: res.message
      })
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const handlePasswordClose = () => {
  passwordDialogVisible.value = false
  userName.value = ''
  userId.value = ''
  newPassword.value = ''
}

const passwordSubmit = async () => {
  if (!newPassword.value.trim()) {
    ElMessage.warning('请输入新密码')
    return
  }
  try {
    const res = await resetPassword({ id: userId.value, newPassword: newPassword.value })
    if (res.code === 1000) {
      ElNotification.success({
        title: '成功',
        message: '重置成功'
      })
      handlePasswordClose()
    } else {
      ElNotification.error({
        title: '错误',
        message: res.message
      })
    }
  } catch (error) {
    console.error('密码重置失败:', error)
  }
}

// 新增用户
const add = () => {
  addUserVisible.value = true
}

const handleCloseAdd = () => {
  addUserVisible.value = false
  addForm.userName = ""
  addForm.loginAccount = ""
  addForm.email = ""
  addForm.tel = ""
  addForm.sex = ""
  addForm.password = ""
  addForm.status = "0"
  addForm.remark = ""
}

const submitAdd = () => {
  // 表单验证
  if (!addForm.userName.trim()) {
    ElMessage.error('请输入用户昵称')
    return
  }
  if (!addForm.loginAccount.trim()) {
    ElMessage.error('请输入登录账号')
    return
  }
  if (!addForm.password.trim()) {
    ElMessage.error('请输入用户密码')
    return
  }
  if (!addForm.tel.trim()) {
    ElMessage.error('请输入手机号码')
    return
  }
  if (!addForm.email.trim()) {
    ElMessage.error('请输入邮箱')
    return
  }
  if (!addForm.sex.trim()) {
    ElMessage.error('请选择性别')
    return
  }
  saveUser(addForm).then(res => {
    if (res.code === 1000) {
      ElNotification.success({
        title: '成功',
        message: '新增用户成功'
      })
      handleCloseAdd()
      query()
    } else {
      ElNotification.error({
        title: '错误',
        message: res.message
      })
    }
  })
}

// 修改用户
const handleCloseUpdate = () => {
  updateUserVisible.value = false
}

const submitUpdate = () => {
  // 表单验证
  if (!updateForm.userName.trim()) {
    ElMessage.error('请输入用户昵称')
    return
  }
  if (!updateForm.loginAccount.trim()) {
    ElMessage.error('请输入登录账号')
    return
  }
  if (!updateForm.password.trim()) {
    ElMessage.error('请输入用户密码')
    return
  }
  if (!updateForm.tel.trim()) {
    ElMessage.error('请输入手机号码')
    return
  }
  if (!updateForm.email.trim()) {
    ElMessage.error('请输入邮箱')
    return
  }
  if (!updateForm.sex.trim()) {
    ElMessage.error('请选择性别')
    return
  }
  editUser(updateForm).then(res => {
    if (res.code === 1000) {
      ElNotification.success({
        title: '成功',
        message: '修改用户成功'
      })
      handleCloseUpdate()
      query()
    } else {
      ElNotification.error({
        title: '错误',
        message: res.message
      })
    }
  })
}
</script>

<style lang="scss" scoped>
@import "@/assets/css/user.css";
</style>
