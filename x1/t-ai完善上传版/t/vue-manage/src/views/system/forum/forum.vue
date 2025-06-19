<template>
  <div class="_forum">
    <div class="search-table">
      <div class="search">
        <el-row :gutter="10" style="padding: 10px">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">标题:</span>
            <el-input
                style="margin-top: 10px"
                size="mini"
                placeholder="请输入标题"
                v-model="search.title"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-button
                style="margin-top: 10px"
                size="mini"
                type="primary"
                @click="searchPage"
            >
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button
                style="margin-top: 10px"
                size="mini"
                @click="refresh"
            >
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-col>
        </el-row>
      </div>
      <div class="table">
        <el-row style="padding-top: 10px; margin-left: 10px">
          <el-button type="primary" size="mini" plain @click="add">
            <el-icon><Plus /></el-icon>新增
          </el-button>
          <el-button
              type="danger"
              :disabled="selectedIds.length <= 0"
              size="mini"
              plain
              @click="deleteDataBtn"
          >
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
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="createTime" label="创建时间" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" type="success" @click="updateData(scope.row.id)">修改</el-button>
              <el-popconfirm
                  confirm-button-text="确认"
                  cancel-button-text="取消"
                  icon-color="red"
                  title="确认删除选中的数据？"
                  @confirm="deleteData(scope.row.id)"
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

    <!-- 新增模态框 -->
    <el-dialog v-model="addVisible" title="新增资讯" width="500px">
      <el-form :model="addForm" :rules="rules" ref="addFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="addForm.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="addForm.content" type="textarea" placeholder="请输入内容" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修改模态框 -->
    <el-dialog v-model="updateVisible" title="修改资讯" width="500px">
      <el-form :model="updateForm" :rules="rules" ref="updateFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="updateForm.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="updateForm.content" type="textarea" placeholder="请输入内容" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="updateVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpdate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Plus, Search, Refresh } from '@element-plus/icons-vue'
import {
  getForumPage,
  removeForum,
  getForumById,
  saveForum,
  editForum
} from '@/api/manage'

// 响应式状态
const loading = ref(true)
const selectedIds = ref([])
const updateId = ref('')
const addVisible = ref(false)
const updateVisible = ref(false)
const tableData = ref([])
const total = ref(0)

const search = reactive({
  title: '',
  pageNumber: 1,
  pageSize: 10
})

// 新增表单相关
const addForm = reactive({
  title: '',
  content: ''
})
const addFormRef = ref(null)

// 修改表单相关
const updateForm = reactive({
  title: '',
  content: ''
})
const updateFormRef = ref(null)

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

// 方法
const query = async () => {
  try {
    const res = await getForumPage(search)
    if (res.code === 1000) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const searchPage = () => {
  search.pageNumber = 1
  query()
}

const refresh = () => {
  search.title = ''
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

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const add = () => {
  addForm.title = ''
  addForm.content = ''
  addVisible.value = true
}

const updateData = async (id) => {
  updateId.value = id
  try {
    const res = await getForumById({ id })
    if (res.code === 1000) {
      updateForm.title = res.data.title
      updateForm.content = res.data.content
      updateVisible.value = true
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error(error)
  }
}

const deleteDataBtn = () => {
  ElMessageBox.confirm(
      `确定删除选中的${selectedIds.value.length}条数据?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    deleteData(selectedIds.value.join(','))
  })
}

const deleteData = async (ids) => {
  try {
    const res = await removeForum({ ids })
    if (res.code === 1000) {
      ElMessage.success('删除成功')
      query()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error(error)
  }
}

// 新增提交
const submitAdd = () => {
  addFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await saveForum(addForm)
        if (res.code === 1000) {
          ElMessage.success('新增成功')
          addVisible.value = false
          query()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        console.error(error)
      }
    }
  })
}

// 修改提交
const submitUpdate = () => {
  updateFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const params = {
          id: updateId.value,
          title: updateForm.title,
          content: updateForm.content
        }
        const res = await editForum(params)
        if (res.code === 1000) {
          ElMessage.success('修改成功')
          updateVisible.value = false
          query()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        console.error(error)
      }
    }
  })
}

// 生命周期
onMounted(() => {
  query()
})
</script>

<style lang="scss" scoped>
@import "@/assets/css/forum.css";
</style>
