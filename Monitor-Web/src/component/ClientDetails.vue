<script setup>
import {computed, reactive, watch} from "vue";
import {get, post} from "@/net";
import {cpuNameToImage, fitByUnit, osNameToIcon, percentageToStatus} from "@/tools";
import {useClipboard} from "@vueuse/core";
import {ElMessage} from "element-plus";
import RuntimeHistory from "@/component/RuntimeHistory.vue";
import {Delete} from "@element-plus/icons-vue";
import {useStore} from "@/store";

const locations = [
  {name: 'cn', desc: '中国大陆'},
  {name: 'hk', desc: '香港'},
  {name: 'jp', desc: '日本'},
  {name: 'us', desc: '美国'},
  {name: 'sg', desc: '新加坡'},
  {name: 'kr', desc: '韩国'},
  {name: 'de', desc: '德国'}
]

const emits = defineEmits(['delete'])

const props = defineProps({
  id: Number,
  update: Function
})

const details = reactive({
  base: {},
  runtime: {
    list: []
  },
  editNode: false
})

const nodeEdit = reactive({
  name: '',
  location: ''
})

const enableNodeEdit = () => {
  details.editNode = true
  nodeEdit.location = details.base.location
  nodeEdit.name = details.base.node
}

const {copy} = useClipboard()

const copyIp = () => copy(details.base.ip).then(() => ElMessage.success("成功复制IP地址到剪贴板"))

function updateDetails() {
  props.update()
  init(props.id)
}

function deleteClient() {
  ElMessageBox.confirm('删除此主机后所有统计数据都将丢失，您确定要这样做吗？', '删除主机', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    get(`/api/monitor/delete?clientId=${props.id}`, () => {
      emits('delete')
      props.update()
      ElMessage.success('主机已成功移除')
    })
  }).catch(() => {})
}

setInterval(() => {
  if(props.id !== -1 && details.runtime) {
    get(`/api/monitor/runtime-now?clientId=${props.id}`, data => {
      if(details.runtime.list.length >= 360)
        details.runtime.list.splice(0, 1)
      details.runtime.list.push(data)
    })
  }
}, 10000)

const now = computed(() => details.runtime.list[details.runtime.list.length - 1])

const submitNodeEdit = () => {
  post(`/api/monitor/node`, {
    id: props.id,
    node: nodeEdit.name,
    location: nodeEdit.location,
  }, () => {
    console.log(nodeEdit.name)
    details.editNode = false;
    updateDetails()
    ElMessage.success("节点更新成功")
  })
}

const init = id => {
  if(id !== -1) {
    details.base = {}
    details.runtime = { list: [] }
    get(`/api/monitor/details?clientId=${id}`, data => Object.assign(details.base, data))
    get(`/api/monitor/runtime-history?clientId=${id}`, data => Object.assign(details.runtime, data))
  }
}

watch(() => props.id, init, { immediate: true })

const store = useStore()
</script>

<template>
  <el-scrollbar>
    <div class="client-details" v-loading="Object.keys(details.base).length === 0">
      <div v-if="Object.keys(details.base).length">
        <div style="display: flex;justify-content: space-between">
          <div class="title">
            <i class="fa-solid fa-server"></i>
            <span> 服务器信息</span>
          </div>
          <el-button :icon="Delete" type="danger" text plain @click="deleteClient" v-if="store.isAdmin">删除此主机</el-button>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div class="details-list">
          <div>
            <span>服务器ID</span>
            <span>{{details.base.id}}</span>
          </div>
          <div>
            <span>服务器名称</span>
            <span>{{details.base.name}}</span>
          </div>
          <div>
            <span>运行状态</span>
            <span>
            <i style="color: #18cb18" class="fa-solid fa-circle-pause" v-if="details.base.online"></i>
            <i style="color: #d52727" class="fa-solid fa-circle-play" v-else></i>
            {{details.base.online ? '运行中' : '离线'}}
          </span>
          </div>
          <div v-if="!details.editNode">
            <span>服务器节点</span>
            <span>
            <i :class="`flag-icon flag-icon-${details.base.location}`"></i>
            {{details.base.node}}
          </span>
            <i class="fa-solid fa-pen-to-square interact-item" style="margin-left: 10px" @click.stop="enableNodeEdit"></i>
          </div>
          <div v-else>
            <span>服务器节点</span>
            <div style="display: inline-block;height: 15px">
              <div style="display: flex">
                <el-select v-model="nodeEdit.location" style="width: 80px" size="small">
                  <el-option v-for="item in locations" :value="item.name">
                    <span :class="`flag-icon flag-icon-${item.name}`"></span>&nbsp;
                    {{item.desc}}
                  </el-option>
                </el-select>
                <el-input v-model="nodeEdit.name" style="margin-left: 10px"
                          size="small" placeholder="请输入节点名称..."/>
                <div style="margin-left: 10px">
                  <i @click.stop="submitNodeEdit" class="fa-solid fa-check interact-item"/>
                </div>
              </div>
            </div>
          </div>
          <div>
            <span>IP地址</span>
            <span>
            {{details.base.ip}}
            <i class="fa-solid fa-copy interact-item" @click.stop="copyIp" style="margin-left: 10px;color: darkgray"></i>
          </span>
          </div>
          <div style="display: flex">
            <span>处理器</span>
            <span>{{details.base.cpuName }}</span>
            <el-image style="height: 20px;margin-left: 10px;"
                      :src="`/cpu-icon/${cpuNameToImage(details.base.cpuName)}`"/>
          </div>
          <div>
            <span>硬件配置信息</span>
            <span>
            <i class="fa-solid fa-microchip"></i>
            <span style="margin-right: 10px">{{' '+details.base.cpuCore}} CPU / </span>
            <i class="fa-solid fa-memory"></i>
            <span> {{ ` ${details.base.memory.toFixed(1)} GB` }}</span>
          </span>
          </div>
          <div>
            <span>操作系统</span>
            <i :style="{color:osNameToIcon(details.base.osName).color}"
               :class="`fa-brands ${osNameToIcon(details.base.osName).icon}`"></i>
            <span style="margin-left: 4px">{{details.base.osName+' '+details.base.osVersion}}</span>
          </div>
        </div>
        <div class="title" style="margin-top: 20px">
          <i class="fa-solid fa-gauge-high"></i>
          <span> 实时监控</span>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div v-if="details.base.online" v-loading="!details.runtime.list.length"
             style="min-height: 200px">
          <div style="display: flex" v-if="details.runtime.list.length">
            <el-progress type="dashboard" :width="100" :percentage="now.cpuUsage * 100"
                         :status="percentageToStatus(now.cpuUsage * 100)">
              <div style="font-size: 17px;font-weight: bold;color: initial">CPU</div>
              <div style="font-size: 13px;color: grey;margin-top: 5px">{{ `${(now.cpuUsage * 100).toFixed(2)}%` }}</div>
            </el-progress>
            <el-progress style="margin-left: 20px" type="dashboard" :width="100"
                         :percentage="now.memoryUsage / details.runtime.memory * 100"
                         :status="percentageToStatus(now.memoryUsage / details.runtime.memory * 100)">
              <div style="font-size: 16px;font-weight: bold;color: initial">内存</div>
              <div style="font-size: 13px;color: grey;margin-top: 5px">{{now.memoryUsage.toFixed(2)}} GB</div>
            </el-progress>
            <div style="display: flex;flex: 1;flex-direction: column;height: 80px;margin-left: 30px">
              <div style="flex: 1;font-size: 14px">
                <div>实时网络速度</div>
                <div>
                  <i style="color: coral" class="fa-solid fa-arrow-up"></i>
                  <span>{{` ${fitByUnit(now.networkUpload, "KB")}/s`}}</span>
                  <el-divider direction="vertical"/>
                  <i style="color: dodgerblue" class="fa-solid fa-arrow-down"></i>
                  <span>{{` ${fitByUnit(now.networkDownload, "KB")}/s`}}</span>
                </div>
              </div>
              <div>
                <div style="display: flex;font-size: 13px;justify-content: space-between">
                  <div>
                    <i class="fa-solid fa-hard-drive"></i>
                    <span> 磁盘总容量</span>
                  </div>
                  <div>{{`${(now.diskUsage).toFixed(1)}GB / ${(details.runtime.disk).toFixed(1)}GB`}}</div>
                </div>
                <el-progress type="line" :status="percentageToStatus(now.diskUsage/details.runtime.disk*100)"
                             :percentage="now.diskUsage/details.runtime.disk*100" :show-text="false"/>
              </div>
            </div>
          </div>
          <runtime-history style="margin-top: 20px" :data="details.runtime.list"/>
        </div>
        <el-empty description="服务器处于离线状态, 请检查服务器是否正常运行" v-else/>
      </div>
    </div>
  </el-scrollbar>
</template>

<style scoped>
.interact-item{
  transition: .3s;

  &:hover{
    cursor: pointer;
    scale: 1.1;
    opacity: 0.8;
  }
}

.client-details{
  height: 100%;
  padding: 20px;

  .title{
    font-size: 18px;
    color: #57b8f5;
    font-weight: bold;
  }

  .details-list{
    font-size: 14px;

    & div{
      margin-bottom: 10px;

      & span:first-child{
        font-size: 13px;
        color: grey;
        font-weight: normal;
        width: 120px;
        display: inline-block;
      }

      & span{
        font-weight: bold;
        color: var(--el-text-color-primary);
        opacity: 95%;
      }
    }
  }
}
</style>