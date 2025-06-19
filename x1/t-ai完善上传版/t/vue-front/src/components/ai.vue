<script>
export default {
  name: "ai",
};
</script>

<template>
  <router-link to="/ai" class="floating-robot" style="text-decoration: none;">
    <img src="../assets/imgs/机器人.png" alt="Robot" style="width: 80px;height: auto" />
    <p class="robot-text">点击我智能规划旅游路线</p>
  </router-link>
</template>

<style scoped>
.floating-robot {
  position: fixed; /* 固定定位 */
  right: 20px; /* 保持在屏幕右侧，距离右边20px */
  top: 50%; /* 初始位置在屏幕中间 */
  transform: translateY(-50%); /* 垂直居中对齐 */
  cursor: pointer;
  animation: float 20s ease-in-out infinite; /* 定义动画 */
  text-align: center; /* 文本居中 */
  display: flex; /* 使用 flex 布局 */
  flex-direction: column; /* 子元素垂直排列 */
  align-items: center; /* 水平居中 */
  z-index: 9999; /* 设置为最高层级 */
  text-decoration: none; /* 确保没有下划线 */
  border: none; /* 移除边框 */
  background: none; /* 移除背景 */
  padding: 0; /* 移除内边距 */
  margin: 0; /* 移除外边距 */
}

.floating-robot img {
  width: 60px;
  height: 60px;
}

.robot-text {
  font-size: 12px; /* 文本字体大小 */
  color: #333; /* 文本颜色 */
  margin-top: 5px; /* 与图标之间的间距 */
  text-decoration: none; /* 确保没有下划线 */
}

/* 定义浮动动画 */
@keyframes float {
  0% {
    top: 0; /* 从屏幕顶部开始 */
    transform: translateY(0); /* 垂直对齐 */
  }
  50% {
    top: 50%; /* 移动到屏幕中间 */
    transform: translateY(-50%); /* 垂直居中对齐 */
  }
  100% {
    top: 100%; /* 移动到屏幕底部 */
    transform: translateY(0); /* 垂直对齐 */
  }
}
</style>
