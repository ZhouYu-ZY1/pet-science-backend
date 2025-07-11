<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :class="{ 'is-collapse': isCollapse }">
      <div class="logo-container">
        <img src="../assets/logo.svg" alt="萌宠视界" class="logo-img" />
        <span v-show="!isCollapse" class="logo-text">萌宠视界管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <template #title>控制台</template>
        </el-menu-item>
        
        <el-sub-menu index="/user">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/user/list">用户列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/product">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>产品管理</span>
          </template>
          <el-menu-item index="/product/category">分类管理</el-menu-item>
          <el-menu-item index="/product/list">产品列表</el-menu-item>
          <el-menu-item index="/product/create">发布产品</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/order">
          <template #title>
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order/list">订单列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/content">
          <template #title>
            <el-icon><Picture /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/content/list">内容列表</el-menu-item>
        </el-sub-menu>
      </el-menu>
      
      <div class="collapse-btn" @click="toggleSidebar">
        <el-icon v-if="isCollapse"><Expand /></el-icon>
        <el-icon v-else><Fold /></el-icon>
      </div>
    </div>
    
    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <div class="navbar">
        <!-- 添加面包屑导航 -->
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item 
              v-for="(item, index) in breadcrumbList" 
              :key="index" 
              :to="index === breadcrumbList.length - 1 ? '' : item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="right-menu">
          <el-dropdown trigger="click">
            <div class="avatar-wrapper">
              <img style="width: 36px;" src="../assets/admin.svg" />
              <span class="user-name">&ensp;管理员</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 内容区 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  Monitor, User, Goods, List, Picture, 
  Expand, Fold, CaretBottom 
} from '@element-plus/icons-vue'

const isCollapse = ref(false)
const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  return route.path
})

// 面包屑导航数据
const breadcrumbList = ref<Array<{title: string, path: string}>>([])

// 根据路由生成面包屑导航
const generateBreadcrumb = () => {
  const { path, meta, matched } = route
  
  // 清空面包屑
  breadcrumbList.value = []
  
  // 添加首页
  breadcrumbList.value.push({
    title: '控制台',
    path: '/dashboard'
  })
  
  // 根据路由匹配添加面包屑
  if (matched && matched.length > 0) {
    matched.forEach(item => {
      if (item.meta && item.meta.title && item.path !== '/dashboard') {
        breadcrumbList.value.push({
          title: item.meta.title as string,
          path: item.path
        })
      }
    })
  }
}

// 监听路由变化，更新面包屑
watch(() => route.path, () => {
  generateBreadcrumb()
}, { immediate: true })

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const logout = () => {
  // 清除登录信息
  localStorage.removeItem('token')
  // 跳转到登录页
  router.push('/login')
}
</script>

<style scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar-container {
  width: 210px;
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
  position: relative;
}

.sidebar-container.is-collapse {
  width: 64px;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 10px;
  background-color: #2b3649;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
  height: calc(100% - 60px);
}

.collapse-btn {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
  color: #bfcbd9;
  cursor: pointer;
  font-size: 20px;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between; /* 修改为两端对齐 */
  padding: 0 20px;
}

/* 添加面包屑容器样式 */
.breadcrumb-container {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin-left: 20px;
}

.right-menu {
  display: flex;
  align-items: center;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-name {
  margin: 0 5px;
  color: #606266;
}

.app-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>