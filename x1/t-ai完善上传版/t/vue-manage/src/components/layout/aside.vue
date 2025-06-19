<template>
  <el-aside :width="isCollapse ? '64px' : '240px'">
    <div class="back-icon" :style="{ width: isCollapse ? '63px' : '239px' }">
      <img class="logo" style="border-radius: 20px; width: auto; height: 80px" src="../../assets/imgs/bg-2.jpg" />
    </div>
    <el-menu
        @select="handleSelect"
        :collapse="isCollapse"
        :collapse-transition="false"
        class="el-menu-vertical"
        :unique-opened="true"
        background-color="#222B40"
        :text-color="textColor"
        :default-active="this.$route.path + '@' + this.$route.name"
    >
      <el-menu-item index="/index@首页">
        <el-icon><PieChart /></el-icon>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="/attractions@景点管理">
        <el-icon><Grid /></el-icon>
        <span>景点管理</span>
      </el-menu-item>
      <el-menu-item index="/order@景点预约">
        <el-icon><Promotion /></el-icon>
        <span>景点预约</span>
      </el-menu-item>
      <el-menu-item index="/line@线路管理">
        <el-icon><Help /></el-icon>
        <span>线路管理</span>
      </el-menu-item>
      <el-menu-item index="/hotel@酒店管理">
        <el-icon><Shop /></el-icon>
        <span>酒店管理</span>
      </el-menu-item>
      <el-menu-item index="/orderHotel@酒店预约">
        <el-icon><Service /></el-icon>
        <span>酒店预约</span>
      </el-menu-item>
      <el-menu-item index="/forum@资讯管理">
        <el-icon><HelpFilled /></el-icon>
        <span>资讯管理</span>
      </el-menu-item>
      <el-menu-item index="/rotations@轮播图管理">
        <el-icon><PictureFilled /></el-icon>
        <span>轮播图管理</span>
      </el-menu-item>
      <el-submenu index="1">
        <template #title>
          <el-icon><Platform /></el-icon>
          <span v-if="!isCollapse">用户管理</span>
        </template>
        <el-menu-item index="/admin@管理员管理">
          <el-icon><Platform /></el-icon>
          <span>管理员管理</span>
        </el-menu-item>
        <el-menu-item index="/user@普通用户管理">
          <el-icon><User /></el-icon>
          <span>普通用户管理</span>
        </el-menu-item>
      </el-submenu>
    </el-menu>
  </el-aside>
</template>

<script>
import {mapState} from 'vuex';

export default {
  name: "Aside",
  data() {
    return {
      isCollapse: false,
      textColor: "#A4B7CC",
    };
  },
  computed: {
    ...mapState({
      activeMenuArrary: (state) => state.menu.activeMenuArrary,
      displayMenus: (state) => state.menu.displayMenus,
    }),
  },
  methods: {
    handleSelect(key) {
      const menu = key.split("@");
      this.$router.push({
        path: menu[0],
      });
      this.$store.commit('menu/setActiveMenu', menu[0]);
      for (let i = 0; i < this.activeMenuArrary.length; i++) {
        if (this.activeMenuArrary[i].url === menu[0]) {
          return;
        }
      }
      const param = {
        name: menu[1],
        url: menu[0],
      };
      this.$store.commit('menu/addActiveMenu', param);
    },
  },
  mounted() {
    this.$store.commit('menu/setActiveMenu', this.$route.path);
    for (let i = 0; i < this.activeMenuArrary.length; i++) {
      if (this.activeMenuArrary[i].url === this.$route.path) {
        return;
      }
    }
    const param = {
      name: this.$route.name,
      url: this.$route.path,
    };
    this.$store.commit('menu/addActiveMenu', param);
  },
};
</script>

<style scoped lang="scss">
.el-aside {
  background-color: #222B40;
}

.back-icon {
  display: flex;
  height: 100px;
  background-color: #222B40;
  justify-content: center;
  align-items: center;
}

.logo {
  width: 150px;
  height: 50px;
}

.el-menu {
  height: calc(100vh - 100px);
  overflow-x: hidden;
  font-weight: 550 !important;
}

.el-menu::-webkit-scrollbar,
.el-aside::-webkit-scrollbar {
  width: 0px;
  height: 0px;
}

.el-menu-vertical:deep(.el-menu-item:hover) {
  color: #0863F2 !important;
  background-color: #001528 !important;
}

.el-menu-vertical:deep(.el-menu-item.is-active) {
  background-color: #001528 !important;
  box-shadow: inset 5px 0 #DDE2FF;
}

.el-menu-item,
.el-submenu__title {
  height: 47px !important;
  line-height: 47px !important;
}

.el-aside .el-menu {
  border-right: none !important;
}

.el-submenu .el-menu-item {
  background-color: #1B2233 !important;
}

.el-submenu .el-submenu:deep(.el-submenu__title) {
  background-color: #1B2233 !important;
}
</style>
