<script setup>
import {fitByUnit, percentageToStatus} from "@/tools";
import {useClipboard} from "@vueuse/core";
import {ElMessage, ElMessageBox} from "element-plus";
import {post} from "@/net";
import {markRaw} from "vue";
import {CircleCheck} from "@element-plus/icons-vue";
const props = defineProps({
  data: Object,
  update: Function,
})

const {copy} = useClipboard()

const copyIp = () => copy(props.data.ip).then(() => ElMessage.success("成功复制IP地址到剪贴板"))

function rename(){
  ElMessageBox.prompt('请输入新的服务器主机名称', '修改名称',
  {
    type: "info",
    icon: markRaw(CircleCheck),
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputValue: props.data.name,
    inputPattern:
        /^[a-zA-Z0-9_\u4e00-\u9fa5]{1,10}$/,
    inputErrorMessage: '名称只能包含中英文字符、数字和下划线',
  }).then(({value}) => post(`/api/monitor/rename`,{
    id: props.data.id,
    name: value
  },() => {
    ElMessage.success('主机名称已更新')
    props.update()
  }))
}

function truncateString(str, maxLength) {
  if (str.length > maxLength) {
    return str.slice(0, maxLength) + '...';
  }
  return str;
}
</script>

<template>
  <div class="instance-card">
    <div style="justify-content: space-between;display: flex">
      <div>
        <div class="name">
          <span :class="`flag-icon flag-icon-${data.location}`"></span>
          <span style="margin: 0 5px">{{data.name}}</span>
          <i class="fa-solid fa-pen-to-square interact-item" @click.stop="rename"></i>
        </div>
        <div class="os">
          操作系统: {{data.osName+' '+data.osVersion}}
        </div>
      </div>
      <div class="status" v-if="data.online">
        <i style="color: #18cb18" class="fa-solid fa-circle-pause"></i>
        <span style="margin-left: 5px">运行中</span>
      </div>
      <div class="status" v-else>
        <i style="color: #d52727" class="fa-solid fa-circle-play"></i>
        <span style="margin-left: 5px">离线</span>
      </div>
    </div>
    <el-divider style="margin: 10px 0"/>
    <div class="network">
      <span>公网IP: {{data.ip}}</span>
      <i class="fa-solid fa-copy interact-item" @click.stop="copyIp" style="margin-left: 10px;color: darkgray"></i>
    </div>
    <div class="cpu-name">
      处理器: {{truncateString(data.cpuName, 30)}}
    </div>
    <div class="hardware">
      <i class="fa-solid fa-microchip"></i>
      <span style="margin-right: 10px">{{' '+data.cpuCore}} CPU</span>
      <i class="fa-solid fa-memory"></i>
      <span> {{ ` ${data.memory.toFixed(1)} GB` }}</span>
    </div>
    <div class="progress">
      <span>{{ `CPU: ${(data.cpuUsage * 100).toFixed(2)} %` }}</span>
      <el-progress :status="percentageToStatus(data.cpuUsage * 100)" :show-text="false" :percentage="data.cpuUsage * 100" :stroke-width="5"/>
    </div>
    <div class="progress">
      <span>内存: <b>{{data.memoryUsage.toFixed(2)}}</b> GB</span>
      <el-progress :status="percentageToStatus(data.memoryUsage/data.memory * 100)" :show-text="false" :percentage="data.memoryUsage/data.memory * 100" :stroke-width="5"/>
    </div>
    <div class="network-flow">
      <div>网络流量</div>
      <div>
        <i class="fa-solid fa-upload"></i>
        <span>{{' ' + fitByUnit(data.networkUpload,"KB") + '/s'}}</span>
        <el-divider direction="vertical"/>
        <i class="fa-solid fa-download"></i>
        <span>{{' ' + fitByUnit(data.networkDownload,"KB") + '/s'}}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>

.dark .instance-card{
  color: #d9d9d9;
}

.interact-item{
  transition: .3s;

  &:hover{
    cursor: pointer;
    scale: 1.1;
    opacity: 0.8;
  }
}

.instance-card {
  width: 320px;
  padding: 15px;
  background-color: var(--el-bg-color);
  border-radius: 5px;
  box-sizing: border-box;
  color: #5a5a5a;
  transition: .3s;

  &:hover{
    cursor: pointer;
    scale: 1.02;
  }

  .name{
    font-weight: bold;
    font-size: 15px;
  }

  .status{
    font-size: 14px;
  }

  .os{
    font-size: 13px;
    color: grey;
  }

  .network{
    font-size: 13px;
  }

  .cpu-name{
    font-size: 12px;
  }

  .hardware{
    font-size: 13px;
    margin-top: 3px;
  }

  .progress{
    font-size: 12px;
    margin-top: 7px;
  }

  .network-flow{
    font-size: 12px;
    margin-top: 10px;
    justify-content: space-between;
    display: flex;
  }
}
</style>