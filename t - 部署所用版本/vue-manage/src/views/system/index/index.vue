<template>
  <div class="index">
    <div class="stats">
      <div class="stat-item">
        <p>
          <el-icon><User /></el-icon>
          用户总数： {{ shuju.userTotal }}  人
        </p>
      </div>
      <div class="stat-item">
        <p>
          <el-icon><ChatDotRound /></el-icon>
          评论总数： {{ shuju.commentTotal }}  条
        </p>
      </div>
    </div>
    <div class="index1" id="index1"></div>
    <div class="index1" id="index2"></div>
  </div>
</template>

<script>
import { getManageData } from '../../../api/manage.js';
import * as echarts from "echarts";
export default {
  data() {
    return {
      myChart: null,
      myChart1: null,
      shuju: {},
    };
  },
  methods: {
    init() {
      getManageData().then((res) => {
        if (res.code == 1000) {
          this.shuju = res.data;
          var chartDom = document.getElementById('index1');
          this.myChart = echarts.init(chartDom);
          var option = {
            title: {
              text: '近七日景点预约',
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'cross',
                label: {
                  backgroundColor: '#6a7985',
                },
              },
            },
            legend: {
              data: ['预约数'],
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true,
            },
            xAxis: [
              {
                type: 'category',
                boundaryGap: false,
                data: this.shuju.dates,
              },
            ],
            yAxis: [
              {
                type: 'value',
              },
            ],
            series: [
              {
                name: '预约数',
                type: 'line',
                stack: 'Total',
                label: {
                  show: true,
                  position: 'top',
                },
                areaStyle: {},
                emphasis: {
                  focus: 'series',
                },
                data: this.shuju.nums,
              },
            ],
          };
          this.myChart.setOption(option);

          var chartDom1 = document.getElementById('index2');
          this.myChart1 = echarts.init(chartDom1);
          var option1 = {
            title: {
              text: '近七日酒店预约',
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'cross',
                label: {
                  backgroundColor: '#6a7985',
                },
              },
            },
            legend: {
              data: ['预约数'],
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true,
            },
            xAxis: [
              {
                type: 'category',
                boundaryGap: false,
                data: this.shuju.dates,
              },
            ],
            yAxis: [
              {
                type: 'value',
              },
            ],
            series: [
              {
                name: '预约数',
                type: 'bar',
                label: {
                  show: true,
                  position: 'top',
                },
                barWidth: 30,
                itemStyle: {
                  color: 'green',
                },
                barCategoryGap: '20%',
                data: this.shuju.orders,
              },
            ],
          };
          this.myChart1.setOption(option1);
        }
      });
    },
  },
  created() {
  },
  mounted() {
    this.init();
  },
};
</script>

<style scoped>
.index {
  width: 100%;
  height: 100%;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.index1 {
  width: 100%;
  height: 49%;
  margin-bottom: 1%;
  background-color: #ffffff;
  box-shadow: rgba(100, 100, 111, 0.2) 0px 7px 29px 0px;
}

.stats {
  display: flex;
  justify-content: space-between;
  padding: 20px 0;
  background-color: #f5f5f5;
}

.stat-item {
  height: auto;
  width: 200px;
  text-align: center;
  background-color: #87c3df;
  color: #fff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: rgba(0, 0, 0, 0.2) 0px 5px 15px;
  margin: 0 5px;
}

.stat-item p {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
}

.stat-item h3 {
  margin: 0;
  font-size: 24px;
}
</style>
