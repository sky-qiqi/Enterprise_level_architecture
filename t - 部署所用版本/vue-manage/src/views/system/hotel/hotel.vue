<template>
  <div class="_hotel">
    <!-- 搜索和表格区域 -->
    <div class="search-table">
      <div class="search">
        <el-row :gutter="10" style="padding:10px">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">名称:</span>
            <el-input
                style="margin-top:10px"
                size="mini"
                placeholder="请输入名称"
                v-model="search.name"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-button style="margin-top:10px" size="mini" type="primary" @click="searchPage">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button style="margin-top:10px" size="mini" @click="refresh">
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-col>
        </el-row>
      </div>
      <div class="table">
        <el-row style="padding-top:10px;margin-left:10px">
          <el-button type="primary" size="mini" @click="add" plain>
            <el-icon><Plus /></el-icon>新增
          </el-button>
          <el-button type="danger" :disabled="update.length <= 0" size="mini" @click="deleteDataBtn">
            <el-icon><Delete /></el-icon>删除
          </el-button>
        </el-row>
        <el-table
            v-loading="loading"
            :data="tableData"
            @selection-change="handleSelectionChange"
            stripe
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="introduce" label="介绍" />
          <el-table-column prop="state" label="状态">
            <template #default="{ row }">
              <el-tag v-if="row.state === 0">下架</el-tag>
              <el-tag v-if="row.state === 1" type="warning">上架</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" />
          <el-table-column label="操作" width="300">
            <template #default="{ row }">
              <el-button size="small" type="info" v-if="row.state === 0" @click="toEdit(row.id, 1, row.attractionsId)">
                上架
              </el-button>
              <el-button size="small" type="info" v-if="row.state === 1" @click="toEdit(row.id, 0, row.attractionsId)">
                下架
              </el-button>
              <el-button size="small" type="primary" @click="toItem(row.id)">房型</el-button>
              <el-button size="small" type="success" @click="updateData(row.id)">修改</el-button>
              <el-popconfirm
                  title="确认删除选中的数据？"
                  confirm-button-text="确认"
                  cancel-button-text="取消"
                  @confirm="deleteDate(row.id)"
              >
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :page-sizes="[10, 20, 50, 100]"
            v-model:page-size="search.pageSize"
            v-model:current-page="search.pageNumber"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增酒店弹窗 -->
    <el-dialog title="新增酒店" width="40%" :destroy-on-close="true" v-model="addVisible" :before-close="handleCloseAdd">
      <el-form :model="addForm" :rules="rules" ref="addRuleFormRef">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="attractionsId" label="景点名称">
                <el-select size="mini" v-model="addForm.attractionsId" placeholder="请选择景点">
                  <el-option
                      v-for="(item,index) in attractions"
                      :key="index"
                      :label="item.name"
                      :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="name" label="酒店名称">
                <el-input v-model="addForm.name" size="mini" placeholder="请输入名称" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="address" label="地址">
                <el-input v-model="addForm.address" size="mini" placeholder="请输入地址" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="introduce" label="介绍">
                <el-input type="textarea" v-model="addForm.introduce" size="mini" placeholder="请输入介绍" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <div style="width:100%">
              <el-form-item prop="images" label="图片">
                <el-upload
                    :file-list="addFileList"
                    ref="addUpload"
                    :action="uploadImageUrl()"
                    accept="image/*"
                    :multiple="false"
                    :limit="5"
                    :before-upload="beforeAvatorUpload"
                    list-type="picture-card"
                    :on-preview="handlePictureCardPreviewAdd"
                    :on-exceed="handleExceed"
                    :on-success="handleAvatorSuccessAdd"
                    :on-remove="handleRemoveAdd">
                  <i class="el-icon-plus"></i>
                </el-upload>
                <el-dialog title="预览" :visible.sync="addDialogVisible">
                  <img width="100%" :src="addDialogImageUrl" alt="">
                </el-dialog>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer" style="text-align: right">
        <el-button size="mini" type="primary" @click="submitAdd">确 定</el-button>
        <el-button size="mini" @click="handleCloseAdd">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 编辑酒店弹窗 -->
    <el-dialog title="编辑酒店" width="40%" :destroy-on-close="true" v-model="updateVisible" :before-close="handleCloseUpdate">
      <el-form :model="updateForm" :rules="rules" ref="updateRuleFormRef">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="attractionsId" label="景点名称">
                <el-select size="mini" v-model="updateForm.attractionsId" placeholder="请选择景点">
                  <el-option
                      v-for="(item,index) in attractions"
                      :key="index"
                      :label="item.name"
                      :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">

            <div style="width:100%">
              <el-form-item prop="name" label="酒店名称">
                <el-input v-model="updateForm.name" size="mini" placeholder="请输入名称" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">

            <div style="width:100%">
              <el-form-item prop="address" label="地址">
                <el-input v-model="updateForm.address" size="mini" placeholder="请输入地址" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">

            <div style="width:100%" >
              <el-form-item prop="introduce" label="介绍">
                <el-input type="textarea" v-model="updateForm.introduce" size="mini" placeholder="请输入介绍" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">

            <div style="width:100%">
              <el-form-item prop="images" label="图片">
                <el-upload
                    :file-list="updateFileList"
                    ref="updateUpload"
                    :action="uploadImageUrl()"
                    accept="image/*"
                    :multiple="false"
                    :limit="5"
                    :before-upload="beforeAvatorUpload"
                    list-type="picture-card"
                    :on-preview="handlePictureCardPreviewUpdate"
                    :on-exceed="handleExceed"
                    :on-success="handleAvatorSuccessUpdate"
                    :on-remove="handleRemoveUpdate">
                  <i class="el-icon-plus"></i>
                </el-upload>
                <el-dialog title="预览" :visible.sync="updateDialogVisible">
                  <img width="100%" :src="updateDialogImageUrl" alt="">
                </el-dialog>
              </el-form-item>
            </div>
          </el-col>
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
import {ref, reactive, onMounted, watch} from 'vue'
import {useStore} from 'vuex'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Refresh, Plus, Edit, Delete} from '@element-plus/icons-vue'
import {getHotelPage, removeHotel, editHotel, saveHotel, getAttractionsList, getHotelById} from '@/api/manage.js'
import {useRouter} from 'vue-router'

const store = useStore()
const router = useRouter()

// 响应式数据
const loading = ref(true)
const update = ref([])
const remove = ref([])
const updateId = ref('')
const addVisible = ref(false)
const updateVisible = ref(false)
const search = reactive({
  name: '',
  pageNumber: 1,
  pageSize: 5
})
const total = ref(0)
const tableData = ref([])
const attractions = ref([])

// 新增表单相关数据
const addForm = reactive({
  name: "",
  attractionsId: "",
  introduce: "",
  images: "",
  address: ""
})
const addFileList = ref([])
const addDialogImageUrl = ref('')
const addDialogVisible = ref(false)
const rules = {
  name: [
    {required: true, message: '请输入名称', trigger: 'blur'},
  ],
  introduce: [
    {required: true, message: '请输入介绍', trigger: 'blur'},
  ],
  images: [
    {required: true, message: '请上传图片', trigger: 'blur'},
  ],
  address: [
    {required: true, message: '请输入地址', trigger: 'blur'},
  ],
  attractionsId: [
    {required: true, message: '请选择景点', trigger: 'blur'},
  ],
}

// 编辑表单相关数据
const updateForm = reactive({
  name: "",
  attractionsId: "",
  introduce: "",
  images: "",
  address: ""
})
const updateFileList = ref([])
const updateDialogImageUrl = ref('')
const updateDialogVisible = ref(false)

// 表单引用
const addRuleFormRef = ref(null)
const updateRuleFormRef = ref(null)

// 方法
const query = async () => {
  try {
    const res = await getHotelPage(search)
    if (res.code === 1000) {
      tableData.value = res.data.records
      total.value = res.data.total
      loading.value = false
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('请求失败')
  }
}

const handleSelectionChange = (val) => {
  update.value = val.map(item => item.id)
  remove.value = val.map(item => item.id)
}

const searchPage = () => {
  search.pageNumber = 1
  query()
}

const refresh = () => {
  search.name = ''
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

// 新增相关方法
const add = () => {
  addVisible.value = true
  getAttractions()
}

const handleCloseAdd = () => {
  addForm.name = ""
  addForm.attractionsId = ""
  addForm.introduce = ""
  addForm.images = ""
  addForm.address = ""
  addFileList.value = []
  addVisible.value = false
}

// 定义 uploadImageUrl 方法
const uploadImageUrl = () => {
  // 返回你的上传图片的 API 地址
  return store.state.configure.HOST + "/common/uploadImg"; // 使用 mixin 中的定义
}

const handleExceed = (files, fileList) => {
  ElMessage.warning("当前文件数量超出上传限制");
}

const beforeAvatorUpload = (file) => {
  const isLt2M = (file.size / 1024 / 1024) < 2;
  if (!isLt2M) {
    ElMessage.error('上传图片单张大小不能超过2MB');
    return false;
  }
  return true;
}

const handleAvatorSuccessAdd = (res) => {
  if (res.code == 1000) {
    ElMessage({
      type: 'success',
      message: '上传成功!'
    });
    addFileList.value.push({url: store.state.configure.HOST + res.message})
  } else {
    ElMessage.error(res.message)
  }
}

const handleRemoveAdd = (file, fileList) => {
  addFileList.value = fileList
}

const handlePictureCardPreviewAdd = (file) => {
  addDialogImageUrl.value = file.url
  addDialogVisible.value = true
}

const submitAdd = () => {
  const file = addFileList.value.map(item => item.url).join(',')
  addForm.images = file
  addRuleFormRef.value.validate((valid) => {
    if (valid) {
      saveHotel(addForm).then(res => {
        if (res.code == 1000) {
          ElMessage.success({
            message: "保存成功"
          })
          handleCloseAdd()
          query()
        } else {
          ElMessage.error(res.message)
        }
      })
    }
  })
}

// 编辑相关方法
const updateData = (id) => {
  updateId.value = id
  updateVisible.value = true
  getAttractions()
}

const handleCloseUpdate = () => {
  updateForm.name = ""
  updateForm.attractionsId = ""
  updateForm.introduce = ""
  updateForm.images = ""
  updateForm.address = ""
  updateFileList.value = []
  updateVisible.value = false
}

const handleAvatorSuccessUpdate = (res) => {
  if (res.code == 1000) {
    ElMessage({
      type: 'success',
      message: '上传成功!'
    })
    updateFileList.value.push({url: store.state.configure.HOST + res.message})
  } else {
    ElMessage.error(res.message)
  }
}

const handleRemoveUpdate = (file, fileList) => {
  updateFileList.value = fileList
}

const handlePictureCardPreviewUpdate = (file) => {
  updateDialogImageUrl.value = file.url
  updateDialogVisible.value = true
}

const submitUpdate = () => {
  const file = updateFileList.value.map(item => item.url).join(',')
  updateForm.images = file
  updateRuleFormRef.value.validate((valid) => {
    if (valid) {
      editHotel(updateForm).then(res => {
        if (res.code == 1000) {
          ElMessage.success({
            message: "保存成功"
          })
          handleCloseUpdate()
          query()
        } else {
          ElMessage.error(res.message)
        }
      })
    }
  })
}

// 获取景点列表
const getAttractions = () => {
  getAttractionsList().then(res => {
    if (res.code === 1000) {
      attractions.value = res.data
    }
  })
}

// 其他方法
const deleteDataBtn = () => {
  ElMessageBox.confirm(
      `确定删除选中的${remove.value.length}条数据?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    deleteDate(remove.value.join(','))
  }).catch(() => {
  })
}

const deleteDate = async (ids) => {
  try {
    const res = await removeHotel({ids})
    if (res.code === 1000) {
      ElMessage.success('删除成功')
      query()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const toEdit = async (id, state, attractionsId) => {
  try {
    const res = await editHotel({
      id,
      state,
      attractionsId
    })
    if (res.code === 1000) {
      query()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const toItem = (id) => {
  const param = {
    name: '房型管理',
    url: `/hotelItem?id=${id}`
  }
  store.commit('menu/addActiveMenu', param)
  router.push("/hotelItem?id=" + id)
  store.commit('menu/setActiveMenu', `/hotelItem?id=${id}`)
}

// 监听updateId变化
watch(updateId, (newVal) => {
  if (newVal) {
    getHotelById({id: newVal}).then(res => {
      if (res.code == 1000) {
        const data = res.data
        updateForm.name = data.name
        updateForm.attractionsId = data.attractionsId
        updateForm.introduce = data.introduce
        updateForm.address = data.address
        updateForm.id = data.id
        if (data.images) {
          const imgList = data.images.split(",")
          updateFileList.value = imgList.map(img => ({url: img}))
        }
      } else {
        ElMessage.error(res.message)
      }
    })
  }
})

// 生命周期钩子
onMounted(() => {
  query()
})
</script>

<style lang="scss" scoped>
@import "@/assets/css/hotel.css";
</style>
