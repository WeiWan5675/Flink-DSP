<template>
  <div class="logo">
    <router-link :to="{name:'dashboard'}">
      <img src="../../assets/logo.png">
      <h1 v-if="showTitle">{{ title }}</h1>
    </router-link>
  </div>
</template>

<script>
import LogoPng from '@/assets/logo.png'

export default {
  name: 'Logo',
  components: {
    LogoPng
  },
  props: {
    title: {
      type: String,
      default: 'Flink-DSP',
      required: false
    },
    showTitle: {
      type: Boolean,
      default: true,
      required: false
    }
  }
}
</script>
<style scoped>
.sider .logo img {
    height: 52px;
    width: 52px;
    margin-right: -10px;
}
</style>
