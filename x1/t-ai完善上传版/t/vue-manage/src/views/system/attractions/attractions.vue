<template>
  <div class="_attractions">
    <div class="search-table">
      <div class="search">
        <el-row :gutter="10" style="padding:10px">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">名称:</span>
            <el-input style="margin-top:10px" size="mini" placeholder="请输入名称" v-model="search.name"></el-input>
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
        <el-row style="padding-top: 10px; margin-left: 10px">
          <el-button type="primary" size="mini" plain @click="add">
            <el-icon><Plus /></el-icon>新增
          </el-button>
          <el-button type="danger" :disabled="selectedIds.length <= 0" size="mini" plain @click="deleteDataBtn">
            <el-icon><Delete /></el-icon>删除
          </el-button>
        </el-row>
        <el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange" stripe style="width: 100%">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="introduce" label="简介" />
          <el-table-column prop="num" label="库存" />
          <el-table-column prop="state" label="状态">
            <template #default="{ row }">
              <el-tag v-if="row.state === 0">下架</el-tag>
              <el-tag v-else type="warning">上架</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" />
          <el-table-column label="操作" width="300">
            <template #default="{ row }">
              <el-button size="small" type="info" v-if="row.state === 0" @click="toEdit(row.id, 1)">上架</el-button>
              <el-button size="small" type="info" v-else @click="toEdit(row.id, 0)">下架</el-button>
              <el-button size="small" type="primary" @click="toComment(row.id)">评论</el-button>
              <el-button size="small" type="success" @click="updateData(row.id)">修改</el-button>
              <el-popconfirm
                  title="确认删除选中的数据？"
                  confirm-button-text="确认"
                  cancel-button-text="取消"
                  @confirm="deleteData(row.id)"
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
  </div>

  <!-- 新增景点对话框 -->
  <el-dialog
      v-model="addVisible"
      title="新增景点"
      width="600px"
      @closed="resetForm"
  >
    <el-form
        :model="formData"
        :rules="formRules"
        ref="addFormRef"
        label-width="80px"
    >
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入景点名称" />
      </el-form-item>

      <el-form-item label="价格" prop="price">
        <el-input
            v-model.number="formData.price"
            type="number"
            placeholder="请输入价格"
        >
          <template #append>元</template>
        </el-input>
      </el-form-item>

      <el-form-item label="简介" prop="introduce">
        <el-input
            v-model="formData.introduce"
            type="textarea"
            :rows="3"
            placeholder="请输入简介"
        />
      </el-form-item>

      <el-form-item label="介绍" prop="content">
        <el-input
            v-model="formData.content"
            type="textarea"
            :rows="3"
            placeholder="请输入介绍"
        />
      </el-form-item>

      <el-form-item label="库存" prop="num">
        <el-input-number
            v-model.number="formData.num"
            :min="0"
            :max="9999"
            controls-position="right"
        />
      </el-form-item>

      <el-form-item label="实名" prop="realName">
        <el-radio-group v-model="formData.realName">
          <el-radio :label="0">关闭</el-radio>
          <el-radio :label="1">开启</el-radio>
        </el-radio-group>
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

      <el-form-item label="预约须知" prop="open">
        <el-input
            v-model="formData.open"
            type="textarea"
            :rows="3"
            placeholder="请输入预约须知"
        />
      </el-form-item>

      <el-form-item label="入园时间" prop="time">
        <el-input
            v-model="formData.time"
            type="textarea"
            :rows="3"
            placeholder="请输入入园时间"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddForm">确认</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 修改景点对话框 -->
  <el-dialog
      v-model="updateVisible"
      title="修改景点"
      width="600px"
      @closed="resetUpdateForm"
  >
    <el-form
        :model="updateFormData"
        :rules="formRules"
        ref="updateFormRef"
        label-width="80px"
    >
      <el-form-item label="名称" prop="name">
        <el-input v-model="updateFormData.name" placeholder="请输入景点名称" />
      </el-form-item>

      <el-form-item label="价格" prop="price">
        <el-input
            v-model.number="updateFormData.price"
            type="number"
            placeholder="请输入价格"
        >
          <template #append>元</template>
        </el-input>
      </el-form-item>

      <el-form-item label="简介" prop="introduce">
        <el-input
            v-model="updateFormData.introduce"
            type="textarea"
            :rows="3"
            placeholder="请输入简介"
        />
      </el-form-item>

      <el-form-item label="介绍" prop="content">
        <el-input
            v-model="updateFormData.content"
            type="textarea"
            :rows="3"
            placeholder="请输入介绍"
        />
      </el-form-item>

      <el-form-item label="库存" prop="num">
        <el-input-number
            v-model.number="updateFormData.num"
            :min="0"
            :max="9999"
            controls-position="right"
        />
      </el-form-item>

      <el-form-item label="实名" prop="realName">
        <el-radio-group v-model="updateFormData.realName">
          <el-radio :label="0">关闭</el-radio>
          <el-radio :label="1">开启</el-radio>
        </el-radio-group>
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

      <el-form-item label="预约须知" prop="open">
        <el-input
            v-model="updateFormData.open"
            type="textarea"
            :rows="3"
            placeholder="请输入预约须知"
        />
      </el-form-item>

      <el-form-item label="入园时间" prop="time">
        <el-input
            v-model="updateFormData.time"
            type="textarea"
            :rows="3"
            placeholder="请输入入园时间"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="updateVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpdateForm">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue';
import { getAttractionsPage, removeAttractions, editAttractions, getAttractionsById, saveAttractions } from '@/api/manage.js';
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';

const router = useRouter();
const store = useStore();

const loading = ref(true);
const selectedIds = ref([]);
const updateId = ref('');
const addVisible = ref(false);
const updateVisible = ref(false);
const total = ref(0);
const tableData = ref([]);
const dialogVisible = ref(false);
const dialogImageUrl = ref('');
const fileList = ref([]);

const search = reactive({
  name: '',
  pageNumber: 1,
  pageSize: 10
});

const formData = reactive({
  name: '',
  price: null,
  introduce: '',
  content: '',
  num: 0,
  realName: 0,
  images: '',
  open: '',
  time: ''
});

const updateFormData = reactive({
  id: '',
  name: '',
  price: null,
  introduce: '',
  content: '',
  num: 0,
  realName: 0,
  images: '',
  open: '',
  time: ''
});

const formRules = reactive({
  name: [
    { required: true, message: '请输入景点名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, max: 99999, message: '价格范围0-99999', trigger: 'blur' }
  ],
  introduce: [
    { required: true, message: '请输入简介', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入介绍', trigger: 'blur' }
  ],
  num: [
    { type: 'number', min: 0, message: '库存不能小于0', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请选择实名', trigger: 'blur' }
  ],
  images: [
    { required: true, message: '请上传图片', trigger: 'blur' }
  ],
  open: [
    { required: true, message: '请输入预约须知', trigger: 'blur' }
  ],
  time: [
    { required: true, message: '请输入入园时间', trigger: 'blur' }
  ]
});

const addFormRef = ref(null);
const updateFormRef = ref(null);

const query = async () => {
  try {
    const res = await getAttractionsPage(search);
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

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id);
};

const add = () => {
  addVisible.value = true;
};

const toEdit = async (id, state) => {
  try {
    const res = await editAttractions({ id, state });
    if (res.code === 1000) {
      await query();
    }
  } catch (error) {
    console.error(error);
  }
};

const toComment = (id) => {
  const param = {
    name: '景点评论',
    url: `/comments?id=${id}`
  };
  store.commit('menu/addActiveMenu', param);
  router.push(`/comments?id=${id}`);
  store.commit('menu/setActiveMenu', `/comments?id=${id}`);
};

const updateData = async (id) => {
  try {
    const res = await getAttractionsById({ id });
    if (res.code === 1000) {
      Object.assign(updateFormData, res.data);
      updateFormData.num = res.data.num;
      updateFormData.realName = res.data.realName;
      updateFormData.images = res.data.images;
      updateFormData.open = res.data.open;
      updateFormData.time = res.data.time;
      const imgList = res.data.images.split(',');
      imgList.forEach(img => {
        fileList.value.push({ url: img });
      });
      updateVisible.value = true;
    } else {
      ElMessage.error(res.message || '获取数据失败');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('获取数据失败');
  }
};

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
    deleteData(selectedIds.value.join(','));
  });
};

const deleteData = async (ids) => {
  try {
    const res = await removeAttractions({ ids });
    if (res.code === 1000) {
      ElMessage.success('删除成功!');
      await query();
    }
  } catch (error) {
    console.error(error);
  }
};

const submitAddForm = async () => {
  try {
    await addFormRef.value.validate();

    // 将fileList中的图片url整合成一个字符串
    formData.images = fileList.value.map(file => file.url).join(',');

    const res = await saveAttractions(formData);
    if (res.code === 1000) {
      ElMessage.success('新增成功');
      addVisible.value = false;
      await query();
    } else {
      ElMessage.error(res.message || '新增失败');
    }
  } catch (error) {
    console.error('提交失败:', error);
  }
};

const resetForm = () => {
  if (addFormRef.value) {
    addFormRef.value.resetFields();
    formData.num = 0;
    formData.realName = 0;
    formData.images = '';
    formData.open = '';
    formData.time = '';
    fileList.value = [];
  }
};

const submitUpdateForm = async () => {
  try {
    await updateFormRef.value.validate();

    // 将fileList中的图片url整合成一个字符串
    updateFormData.images = fileList.value.map(file => file.url).join(',');

    const res = await editAttractions(updateFormData);
    if (res.code === 1000) {
      ElMessage.success('修改成功');
      updateVisible.value = false;
      await query();
    } else {
      ElMessage.error(res.message || '修改失败');
    }
  } catch (error) {
    console.error('提交修改失败:', error);
  }
};

const resetUpdateForm = () => {
  if (updateFormRef.value) {
    updateFormRef.value.resetFields();
    updateFormData.id = '';
    updateFormData.num = 0;
    updateFormData.realName = 0;
    updateFormData.images = '';
    updateFormData.open = '';
    updateFormData.time = '';
    fileList.value = [];
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

onMounted(() => {
  query();
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/attractions.css";
</style>
