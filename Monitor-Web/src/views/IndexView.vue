<template>
  <el-container class="main-container">
    <el-header class="main-header">
      <el-image style="height: 45px" src="https://www.svgrepo.com/show/530443/interface-control.svg"/>
      <span style="margin-left: 3px;font-weight: bold;font-size: 16px;color: var(--el-text-color)">仪表盘</span>
      <div class="tabs">
        <tab-item v-for="item in tabs" :name="item.name" :active="item.id === tab" @click="changePage(item)"/>
        <el-switch style="margin: 0 20px" v-model="dark"
                   active-color="#424242" :active-action-icon="Moon"
                   :inactive-action-icon="Sunny"/>
        <div style="line-height: 16px;margin-right: 10px;text-align: right">
          <div style="font-size: 16px;font-weight: bold">
            <el-tag type="success" size="small" v-if="store.isAdmin">管理员</el-tag>
            <el-tag v-else size="small">子账户</el-tag>
            {{store.user.username}}
          </div>
          <div style="color: grey;font-size: 13px">{{store.user.email}}</div>
        </div>
        <el-dropdown>
          <el-avatar class="avatar"
                      src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjfRcDr9Qe59D7vuhTk-Rq-4nejF0fFn0QVA&s"/>
          <template #dropdown>
            <el-dropdown-menu @click="userLogout" style="width: 70px;text-align: center;cursor: pointer;">
              <el-icon><Back/></el-icon>
              退出登录
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view v-slot="{ Component }">
        <transition mode="out-in" name="el-fade-in-linear">
          <keep-alive exclude="Security">
            <component :is="Component"/>
          </keep-alive>
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup>
import { logout } from '@/net'
import router from "@/router";
import {Back, Moon, Sunny} from "@element-plus/icons-vue";
import {ref} from "vue";
import {useDark} from "@vueuse/core";
import TabItem from "@/component/TabItem.vue";
import {useRoute} from "vue-router";
import {useStore} from "@/store";

const tabs = [
  {id: 1, name: "管理", route: 'manage'},
  {id: 2, name: "安全", route: 'security'}
]
const route = useRoute()
const defaultIndex = () => {
  for (let tab of tabs) {
    if(route.name === tab.route)
      return tab.id
  }
  return 1;
}

const store = useStore()

const dark = ref(useDark())
const tab = ref(defaultIndex())

function changePage(item){
  tab.value = item.id
  router.push({name: item.route})
}
function userLogout() {
  logout(() => router.push("/"))
}
</script>

<style scoped>
.main-container {
  width: 100vw;
  height: 100vh;

  .main-header{
    height: 55px;
    background-color: var(--el-bg-color);
    border-bottom: solid 1px var(--el-border-color);
    display: flex;
    align-items: center;
  }

  .tabs{
    height: 55px;
    gap: 10px;
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: right;
  }

  .main-content{
    height: 100%;
    background-color: #f4f4f4;
  }
}

.dark .main-container .main-content{
  background-color: #1c1c1c;
}
</style>
