<template>
  <div class="errorpage">
    <img src="@/assets/imgs/404.png" alt="" style="margin-top:10%;width:500px;height:300px">
    <h3>您访问的页面不存在，请查看网络连接是否正常，或查看地址输入是否正确!</h3>
    <el-button @click="toHome">返回首页</el-button>
  </div>
</template>

<script>
export default {
  methods:{
    toHome(){
      this.$router.push('/index')
    }
  }
}
</script>

<style>
.errorpage{
  text-align: center;
}
.errorpage h1{
  margin: 10px;
  font-weight: 300;
}
</style>
