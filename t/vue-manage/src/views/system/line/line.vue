<template>
  <div class="_line">
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
            <el-button
                style="margin-top: 10px"
                size="mini"
                type="primary"
                @click="searchPage"
                icon="Search"
            >查询</el-button>
            <el-button
                style="margin-top: 10px"
                size="mini"
                @click="refresh"
                icon="Refresh"
            >重置</el-button>
          </el-col>
        </el-row>
      </div>
      <div class="table">
        <el-row style="padding-top: 10px; margin-left: 10px">
          <el-button type="primary" size="mini" icon="Plus" plain @click="add">新增</el-button>
          <el-button type="danger" :disabled="update.length <= 0" size="mini" icon="Delete" plain @click="deleteDataBtn">删除</el-button>
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
          <el-table-column prop="temperature" label="温度" />
          <el-table-column prop="geography" label="地理情况" />
          <el-table-column prop="createTime" label="创建时间" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" type="success" @click="updateData(scope.row)">修改</el-button>
              <el-popconfirm
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

    <!-- 新增对话框 -->
    <el-dialog
        v-model="addVisible"
        title="新增线路"
        width="500px"
        @close="addFalse"
    >
      <el-form
          :model="formData"
          label-width="80px"
          :rules="formRules"
          ref="addFormRef"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="介绍" prop="content">
          <el-input
              v-model="formData.content"
              type="textarea"
              :rows="3"
              placeholder="请输入介绍"
          />
        </el-form-item>
        <el-form-item label="简介" prop="introduce">
          <el-input
              v-model="formData.introduce"
              type="textarea"
              :rows="3"
              placeholder="请输入简介"
          />
        </el-form-item>
        <el-form-item label="温度" prop="temperature">
          <el-input v-model.number="formData.temperature" placeholder="请输入温度" />
        </el-form-item>
        <el-form-item label="地理情况" prop="geography">
          <el-input v-model="formData.geography" placeholder="请输入地理情况" />
        </el-form-item>
        <el-form-item label="图片" prop="images">
          <el-upload
              :file-list="fileList"
              ref="upload"
              :action="uploadImageUrl()"
              accept="image/*"
              :multiple="false"
              :limit="5"
              :before-upload="beforeAvatorUpload"
              list-type="picture-card"
              :on-preview="handlePictureCardPreview"
              :on-exceed="handleExceed"
              :on-success="handleAvatorSuccess"
              :on-remove="handleRemove"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <el-dialog title="预览" :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAdd">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改对话框 -->
    <el-dialog
        v-model="updateVisible"
        title="修改线路"
        width="500px"
        :before-close="handleDialogClose"
    >
      <el-form
          :model="updateFormData"
          label-width="80px"
          :rules="formRules"
          ref="updateFormRef"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="updateFormData.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="介绍" prop="content">
          <el-input
              v-model="updateFormData.content"
              type="textarea"
              :rows="3"
              placeholder="请输入介绍"
          />
        </el-form-item>
        <el-form-item label="简介" prop="introduce">
          <el-input
              v-model="updateFormData.introduce"
              type="textarea"
              :rows="3"
              placeholder="请输入简介"
          />
        </el-form-item>
        <el-form-item label="温度" prop="temperature">
          <el-input v-model.number="updateFormData.temperature" placeholder="请输入温度" />
        </el-form-item>
        <el-form-item label="地理情况" prop="geography">
          <el-input v-model="updateFormData.geography" placeholder="请输入地理情况" />
        </el-form-item>
        <el-form-item label="图片" prop="images">
          <el-upload
              :file-list="updateFileList"
              ref="updateUpload"
              :action="uploadImageUrl()"
              accept="image/*"
              :multiple="false"
              :limit="5"
              :before-upload="beforeAvatorUpload"
              list-type="picture-card"
              :on-preview="handlePictureCardPreview"
              :on-exceed="handleExceed"
              :on-success="handleUpdateAvatorSuccess"
              :on-remove="handleUpdateRemove"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <el-dialog title="预览" :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
      </el-form>
      <template #footer>
    <span class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="submitUpdate">确认</el-button>
    </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {ElMessage, ElMessageBox, ElNotification} from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { getLinePage, removeLine, saveLine, editLine } from '@/api/manage.js'
import {useStore} from "vuex";
import {useRouter} from "vue-router";

const router = useRouter();
const store = useStore();

// 响应式数据
const loading = ref(true)
const update = ref([])
const addVisible = ref(false)
const updateVisible = ref(false)
const total = ref(0)
const tableData = ref([])
const fileList = ref([]);
const dialogVisible = ref(false);
const dialogImageUrl = ref('');

const search = reactive({
  name: '',
  pageNumber: 1,
  pageSize: 10
})

const formData = reactive({
  name: '',
  temperature: '',
  geography: '',
  content: '',
  introduce: '',
  images: ''
})

// 修改对话框相关数据
const updateFormData = reactive({
  id: '',
  name: '',
  temperature: '',
  geography: '',
  content: '',
  introduce: '',
  images: ''
})

const updateFileList = ref([]);

// 表单验证规则
const formRules = reactive({
  name: [
    { required: true, message: '名称不能为空', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2到50个字符', trigger: 'blur' }
  ],
  temperature: [
    { required: true, message: '温度不能为空', trigger: 'blur' },
    { type: 'number', message: '温度必须为数字', trigger: 'blur' }
  ],
  geography: [
    { required: true, message: '地理情况不能为空', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '介绍不能为空', trigger: 'blur' }
  ],
  introduce: [
    { required: true, message: '简介不能为空', trigger: 'blur' }
  ],
  images: [
    { required: true, message: '请上传图片', trigger: 'change' }
  ]
})

// 修改对话框打开时的初始化
const updateData = (row) => {
  Object.keys(updateFormData).forEach(key => updateFormData[key] = row[key])
  // 处理图片列表
  if (row.images) {
    updateFileList.value = row.images.split(',').map(url => ({ url }));
  } else {
    updateFileList.value = [];
  }
  originalData.value = { ...updateFormData }; // 保存原始数据副本
  updateVisible.value = true;
};

// 修改对话框图片上传成功处理
const handleUpdateAvatorSuccess = (res) => {
  if (res.code === 1000) {
    ElMessage({
      type: 'success',
      message: '上传成功!'
    });
    const imgUrl = store.state.configure.HOST + res.message;
    updateFileList.value.push({ url: imgUrl });

    // 将图片URL整合到updateFormData.images
    updateFormData.images = updateFileList.value.map(file => file.url).join(',');
  } else {
    ElNotification.error({
      title: '错误',
      message: res.message
    });
  }
};

// 修改对话框图片移除处理
const handleUpdateRemove = (file, fileList) => {
  updateFileList.value = fileList;
  updateFormData.images = fileList.map(file => file.url).join(',');
};

// 提交修改
const submitUpdate = async () => {
  try {
    const res = await editLine(updateFormData);
    if (res.code === 1000) {
      ElMessage.success('修改成功');
      updateVisible.value = false;
      query();
    } else {
      ElNotification.error({ title: '错误', message: res.message });
    }
  } catch (error) {
    ElNotification.error({ title: '错误', message: error.message });
  }
};

// 方法
const query = async () => {
  try {
    const res = await getLinePage(search);
    if (res.code === 1000) {
      tableData.value = res.data.records;
      total.value = res.data.total;
      loading.value = false;
    } else {
      ElNotification.error({
        title: '错误',
        message: res.message
      });
    }
  } catch (error) {
    console.error(error);
  }
};

const add = () => {
  Object.keys(formData).forEach(key => formData[key] = '');
  fileList.value = [];
  addVisible.value = true;
};

const submitAdd = async () => {
  try {
    const res = await saveLine(formData);
    if (res.code === 1000) {
      ElMessage.success('新增成功');
      addVisible.value = false;
      query();
    } else {
      ElNotification.error({ title: '错误', message: res.message });
    }
  } catch (error) {
    ElNotification.error({ title: '错误', message: error.message });
  }
};

const searchPage = () => {
  search.pageNumber = 1;
  query();
};

const refresh = () => {
  search.name = '';
  query();
};

const handleSizeChange = (val) => {
  search.pageSize = val;
  query();
};

const handleCurrentChange = (val) => {
  search.pageNumber = val;
  query();
};

const handleSelectionChange = (val) => {
  update.value = val.map(item => item.id);
};

const addFalse = () => {
  addVisible.value = false;
  query();
};

const updateFalse = () => {
  // 移除未定义的 updateId，重置表单数据
  Object.keys(updateForm).forEach(key => updateForm[key] = '');
  updateVisible.value = false;
  // 不需要在此处调用 query()，除非必要
};

const deleteDataBtn = async () => {
  if (update.value.length === 0) return;
  try {
    await removeLine({ ids: update.value.join(',') });
    ElMessage.success('删除成功!');
    query();
  } catch (error) {
    ElNotification.error({
      title: '错误',
      message: error.message
    });
  }
};

const deleteDate = async (ids) => {
  try {
    await removeLine({ ids });
    ElMessage.success('删除成功!');
    query();
  } catch (error) {
    ElNotification.error({
      title: '错误',
      message: error.message
    });
  }
};

const originalData = ref({}); // 新增：用于保存原始数据

// 检查是否有未保存的更改
const hasChanges = () => {
  return Object.keys(updateForm).some(
      key => updateForm[key] !== originalData.value[key]
  );
};

// 修改对话框关闭处理
const handleDialogClose = (done) => {
  if (hasChanges()) {
    ElMessageBox.confirm('存在未保存的更改，确定要关闭吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      done();
      Object.assign(updateForm, originalData.value); // 恢复原始数据
    }).catch(() => { /* 取消关闭 */ });
  } else {
    done();
  }
};

// 修改取消按钮处理
const handleCancel = () => {
  if (hasChanges()) {
    ElMessageBox.confirm('确定放弃当前修改？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      updateVisible.value = false;
      Object.assign(updateForm, originalData.value); // 恢复原始数据
    });
  } else {
    updateVisible.value = false;
  }
};

const uploadImageUrl = () => {
  return store.state.configure.HOST + "/common/uploadImg";
};

const handleExceed = (files, fileList) => {
  ElMessage.warning("当前文件数量超出上传限制");
};

const beforeAvatorUpload = (file) => {
  const isLt2M = (file.size / 1024 / 1024) < 2;
  if (!isLt2M) {
    ElMessage.error('上传图片单张大小不能超过2MB');
    return false;
  }
  return true;
};

const handlePictureCardPreview = (file) => {
  dialogImageUrl.value = file.url;
  dialogVisible.value = true;
};

const handleAvatorSuccess = (res) => {
  if (res.code === 1000) {
    ElMessage({
      type: 'success',
      message: '上传成功!'
    });
    const imgUrl = store.state.configure.HOST + res.message;
    fileList.value.push({ url: imgUrl });

    // 将图片URL整合到formData.images或updateFormData.images中
    if (addVisible.value) {
      formData.images = fileList.value.map(file => file.url).join(',');
    } else if (updateVisible.value) {
      updateFormData.images = fileList.value.map(file => file.url).join(',');
    }
  } else {
    ElNotification.error({
      title: '错误',
      message: res.message
    });
  }
};

const handleRemove = (file, fileList) => {
  fileList.value = fileList;
};

// 生命周期
onMounted(() => {
  query();
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/line.css";
</style>
