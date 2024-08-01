<script setup>
defineProps({
  name: String,
  active: Boolean,
})
</script>

<template>
  <div :class="`tab-item ${active ? 'active' : ''}`">
    {{name}}
  </div>
</template>

<style scoped>
.tab-item {
  font-size: 15px;
  width: 52px;
  text-align: center;
  box-sizing: border-box;
  transition: color .3s;
  height: 55px;
  line-height: 55px;

  &:hover{
    cursor: pointer;
    color: var(--el-color-primary);
  }
}

.active{
  border-bottom:solid 2px var(--el-color-primary);
}
</style>