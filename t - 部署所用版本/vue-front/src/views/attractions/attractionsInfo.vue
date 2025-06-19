<template>
  <div class="attractionsInfo">
    <ai></ai>
    <headers></headers>
    <div class="attractionsInfo1">
      <div class="attractionsInfo2">
        <div class="attractionsInfo3">
          <el-carousel height="500px" v-if="info.images && info.images.length > 0">
            <el-carousel-item v-for="(item, index) in info.images.split(',')" :key="index">
              <img v-if="item" style="width:100%;height:100%;" :src="item">
              <div v-else style="width:100%;height:100%; background-color: #e9e9e9; text-align: center; line-height: 500px;">
                无效图片链接
              </div>
            </el-carousel-item>
          </el-carousel>
          <div v-else-if="!info.images && id">
            正在加载图片...
          </div>
          <div v-else>
            暂无图片
          </div>
        </div>
        <div class="attractionsInfo4">
          <div class="attractionsInfo5">{{ info.name }}</div>
          <div class="attractionsInfo5" v-if="info.price !== undefined">价格：{{ info.price }}（元）</div>
          <div class="attractionsInfo5" v-else>加载中...</div>
          <div style="margin-left:20px;margin-top:10px">库存：{{ info.num }} - {{ info.realName == 0 ? '非实名' : '实名' }}</div>
          <div class="attractionsInfo6" v-html="info.introduce"></div>
          <div class="attractionsInfo7">
            <el-button size="small" type="primary" plain @click="toOrder"  style="font-size: 18px;">立即预约</el-button>
          </div>
        </div>
      </div>
    </div>
    <div class="attractionsInfo1">
      <div class="attractionsInfo8">
        <div style="margin-left:20px;margin-right:20px;margin-top:20px" v-html="info.content"></div>
        <div style="margin-left:20px;margin-right:20px;margin-top:20px" v-html="info.open"></div>
        <div style="margin-left:20px;margin-right:20px;margin-top:20px;margin-bottom:20px" v-html="info.time"></div>
      </div>
    </div>
    <div class="attractionsInfo1">
      <div class="attractionsInfo8">
        <h2 style="margin-left: 10px">评论</h2>
        <div style="display: flex; align-items: center;">
          <el-input
              style="flex: 1; margin-right: 10px;margin-left: 10px"
              v-model="content"
              type="textarea"
              rows="1"
              placeholder="请输入评论内容"
          ></el-input>
          <el-button
              style="margin-top: 0;margin-right: 10px;font-size: 16px"
              type="primary"
              size="small"
              plain
              @click="saveComments"
          >
            发表评论
          </el-button>
        </div>
        <div class="forum1" style="width:100%">
          <div class="forum2" v-for="(item, index) in tableData" :key="index">
            <img class="avatar" :src="$store.state.HOST + item.avatar">
            <div class="comment-info">
              <div class="comment-author">{{ item.createBy }}</div>
              <div class="comment-content">{{ item.content }}</div>
            </div>
          </div>
          <div v-if="!tableData || tableData.length === 0" style="text-align: center; margin-top: 20px;">暂无评论</div>
        </div>
        <el-pagination
            v-if="total > search.pageSize"
            background
            :page-size="search.pageSize"
            layout="prev, pager, next"
            @current-change="handleCurrentChange"
            :total="total">
        </el-pagination>
      </div>
    </div>
    <el-dialog
        title="预约"
        v-model="dialogVisible"
        width="40%"
    >
      <div style="display: flex; align-items: center; margin-bottom: 10px;">
        <el-input-number
            size="small"
            v-model="num"
            :min="1"
            :max="3"
            @change="handleNumChange"
        ></el-input-number>
        <el-date-picker
            size="small"
            style="margin-left:20px"
            v-model="date1"
            type="date"
            placeholder="选择预约日期"
            :picker-options="pickerOptions"
        ></el-date-picker>
      </div>

      <div class="people-container">
        <div
            class="person-item"
            v-for="(person, index) in people"
            :key="'person-' + index"
            :style="{ marginBottom: index === people.length - 1 ? '0' : '10px' }"
        >
          <div class="person-header">
            预约人 {{ index + 1 }}
          </div>
          <div class="input-group">
            <el-input
                size="small"
                v-model="person.name"
                placeholder="请输入姓名"
                style="margin-right: 10px; flex: 1"
            ></el-input>
            <el-input
                size="small"
                v-model="person.tel"
                placeholder="请输入电话"
                style="margin-right: 10px; flex: 1"
            ></el-input>
            <el-input
                size="small"
                v-if="info.realName === 1"
                v-model="person.idCard"
                placeholder="请输入身份证号"
                style="flex: 1"
            ></el-input>
          </div>
          <div class="action-buttons" v-if="people.length > 1">
            <i
                class="el-icon-remove-outline"
                @click="removePerson(index)"
                style="color: red; margin-left: 10px; cursor: pointer"
            ></i>
          </div>
        </div>

        <div
            class="add-button"
            v-if="people.length < num && people.length < 3"
            @click="addPerson()"
            style="color: #409eff; cursor: pointer; margin-top: 10px"
        >
          <i class="el-icon-circle-plus-outline"></i> 添加预约人
        </div>
      </div>

      <div v-if="num > 0" style="margin-top:10px; color: #909399; font-size: 12px;">
        请填写 {{ num }} 位预约人的信息
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="closeOrder" size="small">取 消</el-button>
        <el-button type="primary" @click="saveOrder" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAttractionsById, saveAttractionOrder, getCommentsPage, saveComments } from '@/api/front.js';
import headers from '@/components/header.vue';
import bottoms from '@/components/bottom.vue';
import $store from "@/store/configure.js";

const components = {
  headers,
  bottoms,
};

export default {
  computed: {
    $store() {
      return $store
    }
  },
  components,
  data() {
    return {
      id: "",
      date1: new Date(),
      dialogVisible: false,
      search: {
        attractionsId: "",
        pageSize: 10,
        pageNumber: 1,
      },
      info: {
        name: "",
        price: 0,
        num: 0,
        realName: 0,
        introduce: "",
        content: "",
        open: "",
        time: "",
        images: null
      },
      total: 0,
      tableData: [],
      content: "",
      num: 1,
      people: [
        {
          name: "",
          tel: "",
          idCard: ""
        }
      ],
      isLoading: false,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7; // 不能选择过去的日期
        }
      }
    };
  },
  methods: {
    saveComments() {
      if (!this.content || this.content.trim() === "") {
        this.$message.warning('请输入评论内容');
        return;
      }

      if (!this.id) {
        this.$message.error('缺少景点ID，无法评论');
        return;
      }

      const param = {
        content: this.content.trim(),
        attractionsId: this.id
      };
      saveComments(param).then(res => {
        if (res.code === 1000) {
          this.$message({
            message: '评论成功',
            type: 'success'
          });
          this.content = "";
          this.search.pageNumber = 1;
          this.getCommentsPage();
        } else {
          this.$message.warning(res.msg || '评论失败');
        }
      }).catch(error => {
        console.error("API request failed for saveComments:", error);
        this.$message.error('评论请求出错');
      });
    },
    getCommentsPage() {
      if (!this.id) {
        console.warn("ID is not set for getCommentsPage");
        this.tableData = [];
        this.total = 0;
        return;
      }
      this.search.attractionsId = this.id;
      getCommentsPage(this.search).then(res => {
        if (res.code === 1000 && res.data && Array.isArray(res.data.records)) {
          this.tableData = res.data.records;
          this.total = typeof res.data.total === 'number' ? res.data.total : 0;
        } else {
          console.error("Error fetching comments:", res.msg || 'Unknown error', res);
          this.$message.error(res.msg || '获取评论列表失败');
          this.tableData = [];
          this.total = 0;
        }
      }).catch(error => {
        console.error("API request failed for getCommentsPage:", error);
        this.$message.error('获取评论列表请求出错');
        this.tableData = [];
        this.total = 0;
      });
    },
    closeOrder() {
      this.date1 = "";
      this.num = 1;
      this.people = [{
        name: "",
        tel: "",
        idCard: ""
      }];
      this.dialogVisible = false;
    },
    saveOrder() {
      if (!this.date1) {
        this.$message.warning('请选择预约日期');
        return;
      }
      if (!this.num || this.num < 1) {
        this.$message.warning('请输入有效的预约数量');
        return;
      }

      if (this.people.length !== this.num) {
        if (this.people.length < this.num) {
          this.$message.warning(`请填写 ${this.num} 位预约人的信息`);
        } else {
          this.$message.warning(`预约人信息超出预约数量，请填写 ${this.num} 位预约人信息`);
        }
        return;
      }

      for (let i = 0; i < this.people.length; i++) {
        const item = this.people[i];
        if (!item.name || item.name.trim() === "") {
          this.$message.warning(`请完善第 ${i + 1} 位预约人的姓名`);
          return;
        }
        if (!item.tel || item.tel.trim() === "") {
          this.$message.warning(`请完善第 ${i + 1} 位预约人的联系方式`);
          return;
        }

        if (this.info.realName === 1 && (!item.idCard || item.idCard.trim() === "")) {
          this.$message.warning(`请完善第 ${i + 1} 位预约人的证件号`);
          return;
        }
      }

      if (!this.id) {
        this.$message.error('缺少景点ID，无法创建订单');
        return;
      }

      const param = {
        attractionsId: this.id,
        num: this.num,
        time: this.date1,
        people: JSON.stringify(this.people)
      };
      saveAttractionOrder(param).then(res => {
        if (res.code === 1000) {
          this.$message({
            message: '预约成功，请等待确认',
            type: 'success'
          });
          this.closeOrder();
        } else {
          this.$message.warning(res.msg || '预定失败');
        }
      }).catch(error => {
        this.$message.error('预定请求出错');
      });
    },
    getAttractionsById() {
      if (!this.id) {
        console.error('景点ID不存在');
        this.$message.error('景点ID不存在，请返回列表页重新选择');
        return;
      }

      this.isLoading = true;

      getAttractionsById({ id: this.id }).then(res => {
        this.isLoading = false;

        if (res.code === 1000 && res.data) {
          this.info = res.data;
          this.normalizeInfoData();
        } else {
          this.$message.error(res.msg || '获取景点信息失败');
          console.error('获取景点信息失败:', res);
        }
      }).catch(error => {
        this.isLoading = false;
        this.$message.error('网络错误，请稍后重试');
        console.error('获取景点信息请求出错:', error);
      });
    },
    isInfoLoaded() {
      return (
          this.info &&
          typeof this.info.realName !== 'undefined' &&
          typeof this.info.num !== 'undefined' &&
          this.info.num > 0
      );
    },
    normalizeInfoData() {
      this.info.price = parseFloat(this.info.price) || 0;
      this.info.num = parseInt(this.info.num) || 0;
      this.info.realName = parseInt(this.info.realName) || 0;

      if (!this.info.images) {
        this.info.images = '';
      }
    },
    toOrder() {
      if (this.isLoading) {
        this.$message.warning('数据加载中，请稍候');
        return;
      }

      if (!this.isInfoLoaded()) {
        this.$message.warning('景点信息不完整，无法预约');
        this.getAttractionsById();
        return;
      }

      this.resetOrderForm();
      this.dialogVisible = true;
    },
    resetOrderForm() {
      this.num = 1;
      this.date1 = new Date();
      this.people = [{
        name: '',
        tel: '',
        idCard: ''
      }];
    },
    handleNumChange(newNum) {
      if (newNum < this.people.length) {
        this.people.splice(newNum);
      } else if (newNum > this.people.length) {
        const addCount = Math.min(newNum - this.people.length, 3 - this.people.length);
        for (let i = 0; i < addCount; i++) {
          this.people.push({ name: '', tel: '', idCard: '' });
        }
      }
    },
    addPerson() {
      if (this.people.length < 3) {
        this.people.push({ name: '', tel: '', idCard: '' });
      } else {
        this.$message.warning('最多只能添加3位预约人');
      }
    },
    removePerson(index) {
      if (this.people.length > 1) {
        this.people.splice(index, 1);
        if (this.people.length < this.num) {
          this.num = this.people.length;
        }
      }
    },
    handleCurrentChange(newPage) {
      this.search.pageNumber = newPage;
      this.getCommentsPage();
    },
  },
  mounted() {
    const id = this.$route.query.id;
    if (id) {
      this.id = id;
      this.getAttractionsById();
      this.getCommentsPage();
    } else {
      this.$message.error('缺少商品ID，无法显示详情');
    }
  }
};
</script>

<style scoped>
@import url('../../assets/css/attractionsInfo.css');
</style>
