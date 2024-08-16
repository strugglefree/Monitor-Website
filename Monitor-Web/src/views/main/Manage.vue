<script setup>

import PreviewCard from "@/component/PreviewCard.vue";
import {computed, reactive, ref} from "vue";
import {get} from "@/net";
import ClientDetails from "@/component/ClientDetails.vue";
import RegisterCard from "@/component/RegisterCard.vue";
import {Plus} from "@element-plus/icons-vue";
import {useRoute} from "vue-router";
import {useStore} from "@/store";
import TerminalWindow from "@/component/TerminalWindow.vue";

const list = ref([])
const route = useRoute()
const store = useStore()

const locations = [
  {name: 'cn', desc: '中国大陆'},
  {name: 'hk', desc: '香港'},
  {name: 'jp', desc: '日本'},
  {name: 'us', desc: '美国'},
  {name: 'sg', desc: '新加坡'},
  {name: 'kr', desc: '韩国'},
  {name: 'de', desc: '德国'}
]
const checkNode = ref([])

const clientList = computed(() => {
  if(checkNode.value.length > 0) {
    return list.value.filter(item => checkNode.value.indexOf(item.location) >= 0)
  }else
    return list.value
})

const updateList = () => {
  if(route.name === "manage")
    get(`/api/monitor/list`, data => list.value = data)
}
setInterval(updateList,10*1000)
updateList()

const detail = reactive({
  show: false,
  id: -1
})
const displayClientDetails = (id) => {
  detail.show = true
  detail.id = id
}
const register = reactive({
  show: false,
  token: ''
})

const refreshToken = () => get(`/api/monitor/register`,
    token => register.token = token)

function openTerminal(id){
  terminal.show = true
  terminal.id = id
  detail.show = false
}
const terminal = reactive({
  show: false,
  id: -1
})
</script>

<template>
  <div class="manage-main">
    <div style="display: flex;justify-content: space-between;align-items: end;">
      <div>
        <div class="title">
          <i class="fa-solid fa-computer"></i>
          管理主机列表
        </div>
        <div class="desc">
          在这里管理所有已经注册的主机实例，实时监控主机运行状态，快速进行管理和操作
        </div>
      </div>
      <div v-if="store.isAdmin">
        <el-button :icon="Plus" plain type="primary" @click="register.show = true">添加新的主机</el-button>
      </div>
    </div>
    <el-divider style="margin: 10px 0"/>
    <div>
      <el-checkbox-group v-model="checkNode">
        <el-checkbox style="margin-right: 15px" v-for="node in locations" :key="node" :label="node.name" border>
          <span :class="`flag-icon flag-icon-${node.name}`"></span>
          <span style="font-size: 13px;margin-left: 10px">{{node.desc}}</span>
        </el-checkbox>
      </el-checkbox-group>
    </div>
    <div class="card-list" v-if="list.length">
      <preview-card v-for="item in clientList" :data="item" :update="updateList" @click="displayClientDetails(item.id)"/>
    </div>
    <el-empty description="您还没有添加任何主机，点击右上角添加一个吧" v-else/>
    <el-drawer size="520" :show-close="false" v-model="detail.show"
               :with-header="false" v-if="list.length" @close="detail.id = -1">
      <client-details :id='detail.id' :update="updateList" @delete="updateList" @terminal="openTerminal"/>
    </el-drawer>
    <el-drawer v-model="register.show" direction="btt" :with-header="false"
               style="width: 600px; margin: 10px auto" size="320" @open="refreshToken">
      <register-card :token="register.token"/>
    </el-drawer>
    <el-drawer style="width: 800px;margin: auto" direction="btt" :size="500"
               :close-on-click-modal="false" v-model="terminal.show">
      <template #header>
        <div>
          <div style="color: dodgerblue;font-size: 18px;font-weight: bold">
            <i class="fa-solid fa-link"></i>
            SSH远程连接
          </div>
          <div style="font-size: 14px;color: grey">
            远程连接的建立将由服务端完成，因此在内网环境下也可以正常使用
          </div>
        </div>
      </template>
      <terminal-window :id="terminal.id"/>
    </el-drawer>
  </div>
</template>

<style scoped>
:deep(.el-drawer){
  margin: 10px;
  height: calc(100% - 20px);
  border-radius: 10px;
}

:deep(.el-drawer__body) {
  padding: 0;
}

.manage-main {
  margin: 0 50px;

  .title{
    font-size: 22px;
    font-weight: bold;
  }

  .desc{
    font-size: 15px;
    color: grey;
  }
}

.card-list{
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
}
</style>