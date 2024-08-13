<script setup>
import {reactive, ref} from "vue";
import {Unlock, Lock, Check, RefreshRight} from "@element-plus/icons-vue";
import {logout, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

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
</script>

<template>
  <div style="display: flex;gap: 10px">
    <div style="flex: 0.5">
      <div class="info-card">
        <el-form @validate="onValidate" style="margin: 20px" :model="form" ref="formRef" :rules="rules">
          <el-form-item label="当前密码:" prop="password">
            <el-input :prefix-icon="Unlock" placeholder="当前密码" type="password" v-model="form.password"/>
          </el-form-item>
          <el-form-item label="  新密码:" prop="new_password">
            <el-input :prefix-icon="Lock" placeholder="新密码" type="password" maxlength="16" v-model="form.new_password"/>
          </el-form-item>
          <el-form-item label="确认密码:" prop="check_password" style="margin-bottom: 25px">
            <el-input :prefix-icon="Check" placeholder="确认密码" type="password" maxlength="16" v-model="form.check_password"/>
          </el-form-item>
          <div>
            <el-button size="small" type="warning" :icon="RefreshRight" @click="resetPassword" plain>立即重置密码</el-button>
          </div>
        </el-form>
      </div>
      <div class="info-card" style="margin-top: 10px"></div>
    </div>
    <div class="info-card" style="flex: 0.5"></div>
  </div>
</template>

<style scoped>
.info-card{
  border-radius: 7px;
  background-color: var(--el-bg-color);
  padding: 20px 25px;
}
</style>