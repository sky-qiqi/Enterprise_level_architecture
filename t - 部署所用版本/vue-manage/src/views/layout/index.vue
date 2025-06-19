<template>
    <div class="common-layout">
        <el-container>
        <el-aside width="240px">
            <Aside/>
        </el-aside>
        <el-container>
            <el-header>
                <Header/>
            </el-header>
            <el-main>
                <Main/>
            </el-main>
        </el-container>
        </el-container>
    </div>
  </template>

  <script>
    import { mapState } from 'vuex'
    // import { verPassword } from '@/api/manage.js'
    import Aside from "../../components/layout/aside"
    import Main from "../../components/layout/main"
    import Header from "../../components/layout/header"

    export default {
      name: "index",
      data() {
        return{
          pass: "",
        }
      },
      components:{
        Aside,
        Main,
        Header
      },
      computed: {
        ...mapState({
            activeMenuArrary: state => state.menu.activeMenuArrary,
            activeMenu: state => state.menu.activeMenu
        })
      },
      methods: {
        leftScroll() {
          var container = document.getElementById('menu');
          // 向左滚动100像素
          container.scrollLeft -= 100;
        },
        rightScroll() {
          var container = document.getElementById('menu');
          // 向右滚动100像素
          container.scrollLeft += 100;
        },
        openMenu(url) {
          if (this.activeMenu !== url) {
            this.$router.push({
              path: url,
            })
            this.$store.commit('menu/setActiveMenu', url)
          }
        },
        closeMenu(url) {
          var index = this.activeMenuArrary.length - 1
          for(let i = 0;i < this.activeMenuArrary.length;i++) {
            if(this.activeMenuArrary[i].url === url) {
              index = i;
              break;
            }
          }
          if(this.activeMenu === url) {
            this.$router.push({
              path: this.activeMenuArrary[index-1].url,
            })
            this.$store.commit('menu/setActiveMenu', this.activeMenuArrary[index-1].url)
          }
          this.$store.commit('menu/reduceActiveMenu', index)
          this.$bus.$emit("clearKeepAlive", url);
        },
      },
      created() {

      },
      mounted() {
        // console.clear()
      }
   }
  </script>

  <style  scoped lang="scss">
  @import "../../assets/css/index.css";
  </style>
