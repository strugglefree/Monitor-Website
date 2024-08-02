<script setup>
import {fitByUnit} from "@/tools";
const props = defineProps({
  data: Object
})
</script>

<template>
  <div class="instance-card">
    <div style="justify-content: space-between;display: flex">
      <div>
        <div class="name">
          <span :class="`flag-icon flag-icon-${data.location}`"></span>
          <span style="margin: 0 5px">{{data.name}}</span>
          <i class="fa-solid fa-pen-to-square"></i>
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
      <i class="fa-solid fa-copy" style="margin-left: 10px;color: darkgray"></i>
    </div>
    <div class="cpu-name">
      处理器: {{data.cpuName}}
    </div>
    <div class="hardware">
      <i class="fa-solid fa-microchip"></i>
      <span style="margin-right: 10px">{{' '+data.cpuCore}} CPU</span>
      <i class="fa-solid fa-memory"></i>
      <span> {{ `${data.memory.toFixed(1)} GB` }}</span>
    </div>
    <div class="progress">
      <span>{{ `CPU: ${(data.cpuUsage * 100).toFixed(2)} %` }}</span>
      <el-progress status="success" :show-text="false" :percentage="data.cpuUsage * 100" :stroke-width="5"/>
    </div>
    <div class="progress">
      <span>内存: <b>{{data.memoryUsage.toFixed(2)}}</b> GB</span>
      <el-progress status="success" :show-text="false" :percentage="data.memoryUsage/data.memory * 100" :stroke-width="5"/>
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
:deep(.el-progress-bar__outer){
  background-color: #18cb1822;
}

:deep(.el-progress-bar__inner){
  background-color: #18cb18;
}

.dark .instance-card{
  color: #d9d9d9;
}
.instance-card {
  width: 320px;
  padding: 15px;
  background-color: var(--el-bg-color);
  border-radius: 5px;
  box-sizing: border-box;
  color: #5a5a5a;

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