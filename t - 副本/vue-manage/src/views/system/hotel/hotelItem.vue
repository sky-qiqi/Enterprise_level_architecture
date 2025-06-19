<template>
  <div class="_item">
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
          <el-button type="primary" size="mini" icon="Plus" plain @click="add">新增</el-button>
          <el-button
              type="danger"
              :disabled="update.length <= 0"
              size="mini"
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
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="num" label="数量" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" type="success" @click="updateData(scope.row.id)">修改</el-button>
              <el-popconfirm
                  confirm-button-text="确认"
                  cancel-button-text="取消"
                  icon-color="red"
                  title="确认删除选中的数据？"
                  @confirm="deleteDate(scope.row.id)"
              >
                <template #reference>
                  <el-button size="small" type="danger" style="margin-left:5px">删除</el-button>
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

    <!-- 新增房型对话框 -->
    <el-dialog title="新增房型" width="40%" :destroy-on-close="true" v-model="addVisible" :before-close="handleCloseAdd">
      <el-form :model="form" :rules="rules" ref="addForm">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="name"  label ='名称'>
                <el-input v-model="form.name" size="mini" placeholder="请输入名称" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="price"  label ='价格'>
                <el-input type="number" v-model="form.price" size="mini" placeholder="请输入价格" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="num"  label ='数量'>
                <el-input type="number" v-model="form.num" size="mini" placeholder="请输入数量" autocomplete="off"></el-input>
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

    <!-- 编辑房型对话框 -->
    <el-dialog title="编辑房型" width="40%" :destroy-on-close="true" v-model="updateVisible" :before-close="handleCloseUpdate">
      <el-form :model="form" :rules="rules" ref="updateForm">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="name" label ='名称'>
                <el-input v-model="form.name" size="mini" placeholder="请输入名称" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="price" label ='价格'>
                <el-input type="number" v-model="form.price" size="mini" placeholder="请输入价格" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <div style="width:100%">
              <el-form-item prop="num" label ='数量'>
                <el-input type="number" v-model="form.num" size="mini" placeholder="请输入数量" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer" style="text-align: right">
        <el-button size="mini" type="primary" @click="submitUpdate">确 定</el-button>
        <el-button size="mini" @click="handleCloseUpdate">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, watch} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Refresh, Plus, Edit, Delete} from '@element-plus/icons-vue'
import {getHotelItemPage, removeHotelItem, saveHotelItem, editHotelItem, getHotelItemById} from '@/api/manage.js'

const loading = ref(true)
const update = ref([])
const updateId = ref('')
const addVisible = ref(false)
const updateVisible = ref(false)
const total = ref(0)
const tableData = ref([])
const addForm = ref(null)
const updateForm = ref(null)

const search = reactive({
  hotelId: '',
  name: '',
  pageNumber: 1,
  pageSize: 10
})

const form = reactive({
  id: '',
  hotelId: '',
  name: '',
  price: '',
  num: ''
})

const rules = {
  name: [{required: true, message: '请输入名称', trigger: 'blur'}],
  price: [{required: true, message: '请输入价格', trigger: 'blur'}],
  num: [{required: true, message: '请输入数量', trigger: 'blur'}]
}

const handleSelectionChange = (val) => {
  update.value = val.map(item => item.id)
}

const searchPage = () => {
  search.pageNumber = 1
  query()
}

const query = async () => {
  try {
    console.log('Query Parameters:', { ...search, _t: Date.now() }); // 添加日志
    const res = await getHotelItemPage({ ...search, _t: Date.now() });
    console.log('API Response:', res); // 添加日志
    if (res.code === 1000) {
      tableData.value = res.data.records || [];
      total.value = res.data.total || 0;
      loading.value = false;
    } else {
      ElMessage.error(res.message);
      loading.value = false;
    }
  } catch (error) {
    console.error('Error:', error); // 添加错误日志
    ElMessage.error('请求失败');
    loading.value = false;
  }
};
const refresh = () => {
  search.hotelId = ''
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

const add = () => {
  addVisible.value = true
  form.hotelId = search.hotelId
  form.name = ''
  form.price = ''
  form.num = ''
}

const submitAdd = () => {
  addForm.value.validate((valid) => {
    if (valid) {
      saveHotelItem(form).then(res => {
        if (res.code === 1000) {
          ElMessage.success('新增成功')
          addVisible.value = false
          query()
        } else {
          ElMessage.error(res.message)
        }
      }).catch(error => {
        ElMessage.error('新增失败')
      })
    }
  })
}

const handleCloseAdd = () => {
  addVisible.value = false
}

const updateData = (id) => {
  updateId.value = id
  updateVisible.value = true
  getHotelItemById({id}).then(res => {
    if (res.code === 1000) {
      form.id = res.data.id
      form.name = res.data.name
      form.price = res.data.price
      form.num = res.data.num
    } else {
      ElMessage.error(res.message)
    }
  }).catch(error => {
    ElMessage.error('获取数据失败')
  })
}

const submitUpdate = () => {
  updateForm.value.validate((valid) => {
    if (valid) {
      editHotelItem(form).then(res => {
        if (res.code === 1000) {
          ElMessage.success('修改成功')
          updateVisible.value = false
          query()
        } else {
          ElMessage.error(res.message)
        }
      }).catch(error => {
        ElMessage.error('修改失败')
      })
    }
  })
}

const handleCloseUpdate = () => {
  updateVisible.value = false
}

const deleteDataBtn = () => {
  if (update.value.length === 0) return
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
  }).catch(() => {
  })
}

const deleteDate = async (ids) => {
  try {
    const res = await removeHotelItem({ids})
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

onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const hotelId = urlParams.get('id');
  if (hotelId) {
    search.hotelId = hotelId;
  } else {
    console.error('URL 中没有 id 参数');
  }
  query();
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/hotelItem.css";
</style>
