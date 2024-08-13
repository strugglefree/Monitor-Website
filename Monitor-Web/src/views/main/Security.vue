<script setup>
import {reactive, ref} from "vue";
import {Unlock, Lock, Check, RefreshRight, Plus} from "@element-plus/icons-vue";
import {get, logout, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";
import CreateSubAccount from "@/component/CreateSubAccount.vue";

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.new_password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}
const rules = {
  password: [
    {required: true, message: '请输入原来的密码', trigger: 'blur'},
  ],
  new_password:[
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
  ],
  check_password:[
    { required: true, validator: validatePassword, trigger: ['blur', 'change'] }
  ]
}
const formRef = ref();
const valid = ref(false)
const form = reactive({
  password: '',
  new_password: '',
  check_password: ''
})
const onValidate = (prop , isValid) => valid.value = isValid

function resetPassword(){
  formRef.value.validate(isValid => {
    if(isValid) {
      post(`/api/user/change-password`, form, ()=>{
        ElMessage.success("更改密码成功!请重新登录")
        logout(() => router.push("/"))
      })
    }
  })
}

const accounts = ref([])
const initSubAccounts = () =>
    get(`/api/user/sub/list`, list => accounts.value = list)

const createAccount = ref(false)

const simpleList = ref([])
get(`/api/monitor/simple-list`,(data)=>{
  simpleList.value = data
  initSubAccounts()
})

function deleteAccount(id) {
  get(`/api/user/sub/delete?uid=${id}`, () => {
    ElMessage.success('子账户删除成功')
    initSubAccounts()
  })
}
</script>

<template>
  <div style="display: flex;gap: 10px">
    <div style="flex: 0.5">
      <div class="info-card">
        <div class="title"><i class="fa-solid fa-key" style="margin-right: 7px"></i>更改密码</div>
        <el-divider style="margin: 10px 0"/>
        <el-form @validate="onValidate" style="margin: 20px" :model="form" ref="formRef" :rules="rules" label-width="100">
          <el-form-item label="当前密码:" prop="password">
            <el-input :prefix-icon="Unlock" placeholder="当前密码" type="password" v-model="form.password"/>
          </el-form-item>
          <el-form-item label="  新密码:" prop="new_password">
            <el-input :prefix-icon="Lock" placeholder="新密码" type="password" maxlength="16" v-model="form.new_password"/>
          </el-form-item>
          <el-form-item label="确认密码:" prop="check_password" style="margin-bottom: 25px">
            <el-input :prefix-icon="Check" placeholder="确认密码" type="password" maxlength="16" v-model="form.check_password"/>
          </el-form-item>
          <div style="margin-top: 10px">
            <el-button size="small" type="info" :icon="RefreshRight" @click="resetPassword">立即重置密码</el-button>
          </div>
        </el-form>
      </div>
      <div class="info-card" style="margin-top: 10px"></div>
    </div>
    <div class="info-card" style="flex: 0.5">
      <div class="title"><i class="fa-solid fa-users" style="margin-right: 7px"></i>子账户管理</div>
      <el-divider style="margin: 10px 0"></el-divider>
      <div v-if="accounts.length" style="text-align: center">
        <div v-for="item in accounts" class="account-card">
          <el-avatar class="avatar" :size="30"
                     src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
          <div style="margin-left: 15px;line-height: 18px;flex: 1">
            <div>
              <span>{{item.username}}</span>
              <span style="font-size: 13px;color: grey;margin-left: 5px">
                管理 {{item.clientList.length}} 个服务器
              </span>
            </div>
            <div style="font-size: 13px;color: grey">{{item.email}}</div>
          </div>
          <el-button type="danger" :icon="Delete"
                     @click="deleteAccount(item.id)" text>删除子账户</el-button>
        </div>
        <el-button :icon="Plus" type="primary"
                   @click="createAccount = true" plain>添加更多子用户</el-button>
      </div>
      <div v-else>
        <el-empty :image-size="100" description="还没有任何子账户哦">
          <el-button :icon="Plus" type="primary" plain @click="createAccount = true">添加子账户</el-button>
        </el-empty>
      </div>
    </div>
    <el-drawer v-model="createAccount" size="350" :with-header="false">
      <create-sub-account :clients="simpleList" @create="createAccount = false;initSubAccounts()"/>
    </el-drawer>
  </div>
</template>

<style scoped>
.info-card{
  border-radius: 7px;
  background-color: var(--el-bg-color);
  padding: 20px 25px;
  height: fit-content;

  .title{
    font-weight: bold;
    font-size: 18px;
    color: dodgerblue;
  }
}

.account-card {
  border-radius: 5px;
  background-color: var(--el-bg-color-page);
  padding: 10px;
  display: flex;
  align-items: center;
  text-align: left;
  margin: 10px 0;
}

:deep(.el-drawer){
  border-radius: 10px;
  height: calc(100% - 20px);
  margin: 10px;
}

:deep(.el-drawer__body){
  margin: 0;
  padding: 0;
}

:deep(.el-drawer rtl open){
  margin: 0;
  padding: 0;
}
</style>