<template>
  <div class="_order">
    <div class="search-table">
      <div class="search">
        <el-row :gutter="10" style="padding: 10px">
          <el-col :span="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">名称:</span>
            <el-input
                style="margin-top: 10px"
                size="mini"
                placeholder="请输入名称"
                v-model="search.name"
            />
          </el-col>
          <el-col :span="24" :sm="12" :md="8" :lg="8" :xl="8">
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
          <el-col :span="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">预约人:</span>
            <el-input
                style="margin-top: 10px"
                size="mini"
                placeholder="请输入预约人"
                v-model="search.createBy"
            />
          </el-col>
          <el-col :span="24" :sm="12" :md="8" :lg="8" :xl="8">
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
              :disabled="selectedRows.length <= 0"
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
            <template #default="row">
              <el-tag v-if="row.state === 0">未确认</el-tag>
              <el-tag v-if="row.state === 1" type="warning">已确认</el-tag>
              <el-tag v-if="row.state === 2">取消中</el-tag>
              <el-tag v-if="row.state === 3" type="warning">已取消</el-tag>
              <el-tag v-if="row.state === 4">取消失败</el-tag>
              <el-tag v-if="row.state === 5">已使用</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="itemName" label="房型" />
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="createBy" label="预约人" />
          <el-table-column prop="createTime" label="创建时间" />
          <el-table-column label="操作" width="400">
            <template #default="scope">
              <el-button size="small" type="success" @click="updateData(scope.row.id)">修改</el-button>
              <el-popconfirm
                  style="margin-left: 5px"
                  confirm-button-text="确认"
                  cancel-button-text="取消"
                  icon-color="red"
                  title="确认删除选中的数据？"
                  @confirm="deleteDate(scope.row.id)"
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

    <!-- 修改对话框 -->
    <el-dialog title="编辑酒店预约" width="40%" :destroy-on-close="true" v-model="updateVisible" :before-close="handleClose">
      <el-form :model="form" :rules="rules" ref="ruleForm">
        <el-row :gutter="10">
          <el-col :span="24">
            <el-form-item prop="hotelId" label="酒店ID" style="width: 100%">
              <el-input v-model="form.hotelId" size="mini" placeholder="酒店id" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="userId" label="用户ID" style="width: 100%">
              <el-input v-model="form.userId" size="mini" placeholder="用户id" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="itemId" label="房型ID" style="width: 100%">
              <el-input v-model="form.itemId" size="mini" placeholder="房型id" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="itemName" label="房型" style="width: 100%">
              <el-input v-model="form.itemName" size="mini" placeholder="房型" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="name" label="名称">
              <el-input v-model="form.name" size="mini" placeholder="名称" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="introduce" label="简介" style="width: 100%">
              <el-input v-model="form.introduce" size="mini" placeholder="简介" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="num" label="人数" style="width: 100%">
              <el-input v-model="form.num" size="mini" placeholder="人数" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="people" label="信息" style="width: 100%">
              <el-input v-model="form.people" size="mini" placeholder="信息" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="price" label="价格" style="width: 100%">
              <el-input v-model="form.price" size="mini" placeholder="价格" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="time" label="预约时间" style="width: 100%">
              <el-date-picker size="mini"
                              v-model="form.time"
                              type="date"
                              placeholder="预约时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="state" label="状态" style="width: 100%">
              <!-- 状态选择框 -->
              <el-select
                  v-model="form.state"
                  placeholder="请选择状态"
                  size="mini"
              >
                <el-option label="0-未确认" value="0" />
                <el-option label="1-已确认" value="1" />
                <el-option label="2-取消中" value="2" />
                <el-option label="3-已取消" value="3" />
                <el-option label="4-取消失败" value="4" />
                <el-option label="5-已使用" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" type="primary" @click="submit">确 定</el-button>
        <el-button size="mini" @click="handleClose">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getHotelOrderPage,
  removeHotelOrder,
  editHotelOrder,
  getHotelOrderById
} from '@/api/manage.js'

// 响应式数据
const loading = ref(true)
const selectedRows = ref([])
const updateVisible = ref(false)
const form = reactive({
  hotelId: '',
  name: '',
  introduce: '',
  images: '',
  num: '',
  time: '',
  people: '',
  state: '',
  userId: '',
  itemId: '',
  itemName: '',
  price: ''
})
const rules = reactive({})
const total = ref(0)

const search = reactive({
  hotelId: '',
  name: '',
  state: '',
  userId: '',
  itemId: '',
  createBy: '',
  createTime: '',
  pageNumber: 1,
  pageSize: 10
})

const tableData = ref([])

// 方法
const query = async () => {
  try {
    const res = await getHotelOrderPage(search)
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

const searchPage = () => {
  search.pageNumber = 1
  query()
}

const refresh = () => {
  Object.assign(search, {
    hotelId: '',
    name: '',
    state: '',
    userId: '',
    itemId: '',
    createBy: '',
    createTime: '',
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

const handleSelectionChange = (rows) => {
  selectedRows.value = rows.map(row => row.id)
}

const updateData = (id) => {
  updateVisible.value = true
  getHotelOrderById({id}).then(res => {
    if (res.code === 1000) {
      Object.assign(form, res.data)
      form.num = res.data.num + ''
      form.state = res.data.state + ''
    } else {
      ElMessage.error(res.message)
    }
  })
}

const deleteDataBtn = () => {
  if (selectedRows.value.length === 0) return
  ElMessageBox.confirm(`确定删除选中的${selectedRows.value.length}条数据?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteDate(selectedRows.value.join(','))
  })
}

const deleteDate = async (ids) => {
  try {
    const res = await removeHotelOrder({ids})
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

const submit = () => {
  editHotelOrder(form).then(res => {
    if (res.code === 1000) {
      ElMessage.success('修改成功')
      updateVisible.value = false
      query()
    } else {
      ElMessage.error(res.message)
    }
  })
}

const handleClose = () => {
  updateVisible.value = false
}

// 生命周期
onMounted(() => {
  query()
})
</script>

<style lang="scss" scoped>
@import "@/assets/css/hotelorder.css";
</style>
