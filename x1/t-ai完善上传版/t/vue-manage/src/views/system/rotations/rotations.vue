<template>
  <div class="_rotations">
    <div class="search-table">
      <div class="search">
        <el-row>
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
          <el-button type="primary" size="mini" plain @click="add">
            <el-icon><Plus /></el-icon>新增
          </el-button>
          <el-button type="danger" :disabled="update.length <= 0" size="mini" plain @click="deleteDataBtn">
            <el-icon><Delete /></el-icon>删除
          </el-button>
        </el-row>
        <el-table
            v-loading="loading"
            :data="tableData"
            @selection-change="handleSelectionChange"
            stripe
            style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="image" label="图片" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="updateData(row.id)">修改</el-button>
              <el-popconfirm
                  style="margin-left:5px"
                  confirm-button-text='确认'
                  cancel-button-text='取消'
                  icon-color="red"
                  title="确认删除选中的数据？"
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
            :page-size="search.pageSize"
            :current-page="search.pageNumber"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :total="total"
        />
      </div>
    </div>

    <!-- 新增轮播图对话框 -->
    <el-dialog title="新增轮播图" width="40%" :destroy-on-close="true" v-model="addVisible" :before-close="handleCloseAdd">
      <el-form :model="addForm" :rules="rules" ref="addRuleForm">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
              <span class="search-title">
                  图片:
              </span>
            <div style="width:100%">
              <el-form-item prop="image" style="margin-bottom:0">
                <el-upload
                    :file-list="addFileList"
                    ref="addUpload"
                    :action="uploadImageUrl()"
                    accept="image/*"
                    :multiple="false"
                    :limit="1"
                    :before-upload="beforeAvatorUpload"
                    list-type="picture-card"
                    :on-preview="handlePictureCardPreviewAdd"
                    :on-exceed="handleExceed"
                    :on-success="handleAvatorSuccessAdd"
                    :on-remove="handleRemoveAdd">
                  <i class="el-icon-plus"></i>
                </el-upload>
                <el-dialog :visible.sync="addDialogVisible">
                  <img width="100%" :src="addDialogImageUrl" alt="">
                </el-dialog>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" type="primary" @click="submitAdd">确 定</el-button>
        <el-button size="mini" @click="handleCloseAdd">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 修改轮播图对话框 -->
    <el-dialog title="编辑轮播图" width="40%" :destroy-on-close="true" v-model="updateVisible" :before-close="handleCloseUpdate">
      <el-form :model="updateForm" :rules="rules" ref="updateRuleForm">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
              <span class="search-title">
                  图片:
              </span>
            <div style="width:100%">
              <el-form-item prop="image" style="margin-bottom:0">
                <el-upload
                    :file-list="updateFileList"
                    ref="updateUpload"
                    :action="uploadImageUrl()"
                    accept="image/*"
                    :multiple="false"
                    :limit="1"
                    :before-upload="beforeAvatorUpload"
                    list-type="picture-card"
                    :on-preview="handlePictureCardPreviewUpdate"
                    :on-exceed="handleExceed"
                    :on-success="handleAvatorSuccessUpdate"
                    :on-remove="handleRemoveUpdate">
                  <i class="el-icon-plus"></i>
                </el-upload>
                <el-dialog :visible.sync="updateDialogVisible">
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
import { ref, reactive, onMounted, watch } from 'vue'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRotationsPage, removeRotations, saveRotations, editRotations, getRotationsById } from '@/api/manage.js'
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';

const router = useRouter();
const store = useStore();
const loading = ref(true)
const update = ref([])
const remove = ref([])
const updateId = ref('')
const addVisible = ref(false)
const updateVisible = ref(false)
const tableData = ref([])
const total = ref(0)

const search = reactive({
  pageNumber: 1,
  pageSize: 10,
  name: ''
})

const query = async () => {
  try {
    const res = await getRotationsPage(search)
    if (res.code === 1000) {
      tableData.value = res.data.records
      total.value = res.data.total
      loading.value = false
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const searchPage = () => {
  search.pageNumber = 1
  query()
}

const refresh = () => query()

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

const add = () => {
  addVisible.value = true
}

const handleCloseAdd = () => {
  addForm.value = {
    image: ""
  }
  addFileList.value = []
  addVisible.value = false
}

const updateData = (id) => {
  updateId.value = id
  updateVisible.value = true
}

const handleCloseUpdate = () => {
  updateForm.value = {
    image: ""
  }
  updateFileList.value = []
  updateVisible.value = false
}

const deleteDataBtn = () => {
  ElMessageBox.confirm(`确定删除选中的${remove.value.length}条数据?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteDate(remove.value.join(','))
  }).catch(() => {})
}

const deleteDate = async (ids) => {
  try {
    const res = await removeRotations({ ids })
    if (res.code === 1000) {
      ElMessage.success('删除成功!')
      query()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(error.message)
  }
}

// 新增相关逻辑
const addForm = ref({
  image: ""
})
const addFileList = ref([])
const addDialogImageUrl = ref('')
const addDialogVisible = ref(false)
const rules = {
  image: [
    { required: true, message: '请上传图片', trigger: 'blur' },
  ],
}

const handleAvatorSuccessAdd = (res) => {
  if(res.code === 1000){
    ElMessage({
      type: 'success',
      message: '上传成功!'
    });
    addFileList.value.push({url:res.message})
    addForm.value.image = res.message
  }else{
    ElMessage.error(res.message)
  }
}

const handleRemoveAdd = (file, fileList) => {
  addForm.value.image = ""
  addFileList.value = []
}

const handlePictureCardPreviewAdd = (file) => {
  addDialogImageUrl.value = file.url
  addDialogVisible.value = true
}

const submitAdd = () => {
  if (!addForm.value.image) {
    ElMessage.error('请上传图片')
    return
  }
  saveRotations(addForm.value).then(res => {
    if(res.code === 1000) {
      ElMessage.success("保存成功")
      handleCloseAdd()
      query()
    } else {
      ElMessage.error(res.message)
    }
  })
}

// 修改相关逻辑
const updateForm = ref({
  image: ""
})
const updateFileList = ref([])
const updateDialogImageUrl = ref('')
const updateDialogVisible = ref(false)

const handleAvatorSuccessUpdate = (res) => {
  if(res.code === 1000){
    ElMessage({
      type: 'success',
      message: '上传成功!'
    });
    updateFileList.value.push({url:res.message})
    updateForm.value.image = res.message
  }else{
    ElMessage.error(res.message)
  }
}

const handleRemoveUpdate = (file, fileList) => {
  updateForm.value.image = ""
  updateFileList.value = []
}

const handlePictureCardPreviewUpdate = (file) => {
  updateDialogImageUrl.value = file.url
  updateDialogVisible.value = true
}

const submitUpdate = () => {
  if (!updateForm.value.image) {
    ElMessage.error('请上传图片')
    return
  }
  editRotations(updateForm.value).then(res => {
    if(res.code == 1000) {
      ElMessage.success("保存成功")
      handleCloseUpdate()
      query()
    } else {
      ElMessage.error(res.message)
    }
  })
}

watch(updateId, (newVal) => {
  if(newVal) {
    getRotationsById({id:newVal}).then(res => {
      if(res.code == 1000) {
        updateForm.value = res.data
        // 确保图片路径是完整的
        const fullPath = store.state.configure.HOST + res.data.image
        updateFileList.value.push({url:fullPath})
      } else {
        ElMessage.error(res.message)
      }
    })
  }
})

onMounted(() => {
  query()
})

// 上传相关方法
const uploadImageUrl = () => {
  return store.state.configure.HOST + "/common/uploadImg";
};

const handleExceed = (files, fileList) => {
  ElMessage.warning("当前文件数量超出上传限制")
}

const beforeAvatorUpload = (file) => {
  const isLt2M = (file.size / 1024 /1024) < 2
  if(!isLt2M){
    ElMessage.error('上传图片单张大小不能超过2MB')
    return false
  }
  return true
}
</script>

<style lang="scss" scoped>
@import "@/assets/css/rotations.css";

</style>
