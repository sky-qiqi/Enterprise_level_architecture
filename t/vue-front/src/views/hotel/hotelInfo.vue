<template>
  <div class="attractionsInfo">
    <headers></headers>
    <div class="attractionsInfo1">
      <div class="attractionsInfo2">
        <div class="attractionsInfo3">
          <el-carousel height="500px">
            <el-carousel-item v-for="(item, index) in info.images.split(',')" :key="index">
              <img style="width:100%;height:100%" :src="item" />
            </el-carousel-item>
          </el-carousel>
        </div>
        <div class="attractionsInfo4">
          <div class="attractionsInfo5">{{ info.name }}</div>
          <div style="margin-top: 10px; margin-left: 20px;">
            所属景区：{{ info.attractions }}
          </div>
          <div style="margin-top: 10px; margin-left: 20px;">
            地址：{{ info.address }}
          </div>
          <div class="attractionsInfo6">{{ info.introduce }}</div>
          <div class="attractionsInfo7" style="margin-left:10px">
            <el-button size="small" type="primary" plain @click="toOrder" style="font-size: 18px;">立即预定</el-button>
          </div>
        </div>
      </div>
    </div>
    <el-dialog
        title="预定"
        v-model="dialogVisible"
        width="40%"
    >
      <div>
        <el-radio
            style="margin-top:10px"
            v-for="(item, index) in hotel"
            :key="index"
            size="small"
            v-model="radio1"
            :label="item.id"
            border
        >
          {{ item.name }} - 数量{{ item.num }} - 价格{{ item.price }}
        </el-radio>
      </div>
      <el-input-number
          style="margin-top:10px"
          size="small"
          v-model="num"
          :min="1"
          :max="10"
          @change="handleNumChange"
      ></el-input-number>
      <el-date-picker
          size="small"
          style="margin-left:20px;margin-top:10px"
          v-model="date1"
          type="date"
          placeholder="选择预定日期"
          :picker-options="pickerOptions"
      ></el-date-picker>

      <div class="attractionsInfo9" v-for="(person, personIndex) in peopleList" :key="personIndex" style="margin-top: 10px;">
        <div class="person-header" style="margin-bottom: 5px;">
          <span>预约人 {{ personIndex + 1 }}</span>
        </div>
        <div class="person-fields">
          <el-input
              size="small"
              v-model="person.name"
              placeholder="请输入姓名"
          ></el-input>
          <el-input
              size="small"
              style="margin-left:10px"
              v-model="person.tel"
              placeholder="请输入电话"
          ></el-input>
          <el-input
              size="small"
              style="margin-left:10px"
              v-model="person.idCard"
              placeholder="请输入身份证号"
          ></el-input>
        </div>
      </div>

      <template #footer>
        <el-button @click="closeOrder" size="small">取 消</el-button>
        <el-button type="primary" @click="saveOrder" size="small">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import 'element-plus/dist/index.css';
import { getHotelById, getHotelItemList, saveHotelOrder } from '@/api/front.js';
import headers from '@/components/header.vue';

export default {
  components: {
    headers,
  },
  data() {
    return {
      id: "",
      dialogVisible: false,
      date1: new Date(),
      num: 1,
      peopleList: [
        {
          name: "",
          tel: "",
          idCard: ""
        }
      ],
      info: {
        images: ""
      },
      hotel: [],
      radio1: "",
      pickerOptions: {
        disabledDate(time) {
          return false; // 不禁用任何日期
        }
      }
    };
  },
  methods: {
    toOrder() {
      this.dialogVisible = true;
    },
    closeOrder() {
      this.date1 = new Date(); // 重置为当前日期
      this.num = 1;
      this.radio1 = '';
      this.peopleList = [
        {
          name: "",
          tel: "",
          idCard: ""
        }
      ];
      this.dialogVisible = false;
    },
    handleNumChange(value) {
      // 根据选择的人数动态调整表单
      if (value > this.peopleList.length) {
        // 添加新的预约人表单
        const addCount = value - this.peopleList.length;
        for (let i = 0; i < addCount; i++) {
          this.peopleList.push({
            name: "",
            tel: "",
            idCard: ""
          });
        }
      } else if (value < this.peopleList.length) {
        // 移除多余的预约人表单
        this.peopleList.splice(value);
      }
    },
    saveOrder() {
      // 验证所有预约人信息
      for (let i = 0; i < this.peopleList.length; i++) {
        const person = this.peopleList[i];
        if (!person.name) {
          this.$message({
            message: `请完善预约人${i + 1}姓名`,
            type: 'warning'
          });
          return;
        }
        if (!person.tel) {
          this.$message({
            message: `请完善预约人${i + 1}联系方式`,
            type: 'warning'
          });
          return;
        }
        if (!person.idCard) {
          this.$message({
            message: `请完善预约人${i + 1}证件号`,
            type: 'warning'
          });
          return;
        }
      }

      if (!this.date1) {
        this.$message({
          message: '请选择预约时间',
          type: 'warning'
        });
        return;
      }

      if (!this.radio1) {
        this.$message({
          message: '请选择房型',
          type: 'warning'
        });
        return;
      }

      const param = {
        hotelId: this.id,
        num: this.num,
        itemId: this.radio1,
        time: this.date1,
        people: JSON.stringify(this.peopleList)
      };

      saveHotelOrder(param).then(res => {
        if (res.code === 1000) {
          this.$message({
            message: '预约成功，请等待确认',
            type: 'success'
          });
          this.closeOrder();
        } else {
          this.$message({
            message: res.message,
            type: 'warning'
          });
        }
      });
    },
    getHotelById() {
      getHotelById({ id: this.id }).then(res => {
        if (res.code === 1000) {
          this.info = res.data;
          if (!this.info.images) {
            this.info.images = "";
          }
        }
      });
    },
    getHotelItemList() {
      getHotelItemList({ id: this.id }).then(res => {
        if (res.code === 1000) {
          this.hotel = res.data;
        }
      });
    }
  },
  mounted() {
    this.id = this.$route.query.id;
    this.getHotelById();
    this.getHotelItemList();
  }
};
</script>

<style scoped>
@import url('../../assets/css/hotelInfo.css');
</style>
