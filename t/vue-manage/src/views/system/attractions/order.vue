<template>
  <div class="_order">
    <div class="search-table">
      <div class="search">
        <el-row :gutter="10" style="padding: 10px">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">名称:</span>
            <el-input
                style="margin-top: 10px"
                size="mini"
                placeholder="请输入名称"
                v-model="search.name"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">状态:</span>
            <el-select
                clearable
                style="margin-top: 10px"
                size="mini"
                v-model="search.state"
                placeholder="请选择"
            >
              <el-option label="未确认" value="0" />
              <el-option label="已确认" value="1" />
              <el-option label="取消中" value="2" />
              <el-option label="已取消" value="3" />
              <el-option label="取消失败" value="4" />
              <el-option label="已使用" value="5" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">预约人:</span>
            <el-input
                style="margin-top: 10px"
                size="mini"
                placeholder="请输入预约人"
                v-model="search.createBy"
            />
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
        <el-row style="padding-top: 10px; margin-left: 10px">
          <el-button
              type="danger"
              :disabled="update.length <= 0"
              size="small"
              icon="Delete"
              plain
              @click="deleteDataBtn"
          >删除</el-button>
        </el-row>
        <el-table
            v-loading="loading"
            :data="tableData"
            @selection-change="handleSelectionChange"
            stripe
            style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="num" label="人数" />
          <el-table-column prop="time" label="预约时间" />
          <el-table-column prop="state" label="状态">
            <template #default="{ row }">
              <el-tag v-if="row.state === 0">未确认</el-tag>
              <el-tag v-if="row.state === 1" type="warning">已确认</el-tag>
              <el-tag v-if="row.state === 2">取消中</el-tag>
              <el-tag v-if="row.state === 3" type="warning">已取消</el-tag>
              <el-tag v-if="row.state === 4">取消失败</el-tag>
              <el-tag v-if="row.state === 5">已使用</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createBy" label="预约人" />
          <el-table-column prop="createTime" label="创建时间" />
          <el-table-column label="操作" width="400">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="updateData(row.id)">修改</el-button>
              <el-popconfirm
                  style="margin-left: 5px"
                  confirm-button-text="确认"
                  cancel-button-text="取消"
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

    <!-- 修改弹窗 -->
    <el-dialog
        title="编辑景点预约信息"
        width="40%"
        :destroy-on-close="true"
        v-model="updateVisible"
    >
      <el-form :model="editForm" :rules="rules" ref="editFormRef">
        <el-row :gutter="10">
          <el-col :span="24">
            <el-form-item label="景点ID" style="width: 100%">
              <el-input v-model="editForm.attractionsId" size="mini" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户ID" style="width: 100%">
              <el-input v-model="editForm.userId" size="mini" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="名称" style="width: 100%">
              <el-input v-model="editForm.name" size="mini" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="简介" style="width: 100%">
              <el-input v-model="editForm.introduce" size="mini" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="人数" style="width: 100%">
              <el-input v-model="editForm.num" size="mini" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="信息" style="width: 100%">
              <el-input v-model="editForm.people" size="mini" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="时间" style="width: 100%">
              <el-date-picker size="mini" v-model="editForm.time" type="date" placeholder="预约时间"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态" style="width: 100%">
              <el-select
                  v-model="editForm.state"
                  placeholder="请选择状态"
                  size="mini"
              >
                <el-option label="0 - 未确认" value="0" />
                <el-option label="1 - 已确认" value="1" />
                <el-option label="2 - 取消中" value="2" />
                <el-option label="3 - 已取消" value="3" />
                <el-option label="4 - 取消失败" value="4" />
                <el-option label="5 - 已使用" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" type="primary" @click="submitEdit">确 定</el-button>
        <el-button size="mini" @click="updateVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElNotification, ElMessageBox } from 'element-plus'
import { getAttractionOrderPage, removeAttractionOrder, editAttractionOrder as apiEdit, getAttractionOrderById } from '../../../api/manage.js'
const loading = ref(true)
const update = ref([])
const updateId = ref('')
const updateVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const editFormRef = ref(null)

const search = reactive({
  attractionsId: '',
  name: '',
  introduce: '',
  images: '',
  num: '',
  people: '',
  state: '',
  userId: '',
  createBy: '',
  createTime: '',
  updateBy: '',
  updateTime: '',
  pageNumber: 1,
  pageSize: 10
})

const editForm = reactive({
  attractionsId: '',
  name: '',
  introduce: '',
  images: '',
  num: '',
  time: '',
  people: '',
  state: '',
  userId: ''
})

const rules = reactive({
  // 可以在这里添加表单验证规则
})

const query = async () => {
  try {
    const res = await getAttractionOrderPage(search)
    if (res.code === 1000) {
      tableData.value = res.data.records
      total.value = res.data.total
      loading.value = false
    } else {
      ElNotification.error({
        title: '错误',
        message: res.message
      })
    }
  } catch (error) {
    console.error(error)
  }
}

const searchPage = () => {
  search.pageNumber = 1
  query()
}

const refresh = () => {
  Object.assign(search, {
    attractionsId: '',
    name: '',
    introduce: '',
    images: '',
    num: '',
    people: '',
    state: '',
    userId: '',
    createBy: '',
    updateBy: '',
    pageNumber: 1,
    pageSize: 10
  })
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
}

const updateFalse = () => {
  updateId.value = ''
  updateVisible.value = false
  query()
}

const updateData = (id) => {
  updateId.value = id
  updateVisible.value = true
  getAttractionOrderById({ id }).then(res => {
    if (res.code === 1000) {
      Object.assign(editForm, res.data)
      editForm.num = res.data.num + ""
      editForm.state = res.data.state + ""
    } else {
      ElNotification.error({
        title: '错误',
        message: res.message
      })
    }
  })
}

const deleteDataBtn = () => {
  ElMessageBox.confirm(
      `确定删除选中的${update.value.length}条数据?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    deleteDate(update.value.join(','))
  })
}

const deleteDate = async (ids) => {
  try {
    const res = await removeAttractionOrder({ ids })
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
    console.error(error)
  }
}

const editAttractionOrder = async (id, state) => {
  try {
    const res = await apiEdit({ id, state })
    if (res.code === 1000) {
      query()
    }
  } catch (error) {
    console.error(error)
  }
}

const submitEdit = () => {
  editFormRef.value.validate((valid) => {
    if (valid) {
      apiEdit(editForm).then(res => {
        if (res.code === 1000) {
          ElMessage.success('修改成功!')
          updateVisible.value = false
          query()
        } else {
          ElNotification.error({
            title: '错误',
            message: res.message
          })
        }
      })
    }
  })
}

onMounted(() => {
  query()
})
</script>

<style lang="scss" scoped>
@import "@/assets/css/asorder.css";
.ellipsis-text {
  display: inline-block;
  max-width: 80%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
