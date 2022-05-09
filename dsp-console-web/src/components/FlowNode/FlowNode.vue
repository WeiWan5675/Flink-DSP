<template>
  <div
    slot="FlowNodeDiv"
    :bordered="false"
    class="FlowNodeDiv"
    @drop="drop"
    @dragover="allowDrop">
    <div class="nodeTitle" >{{ node.nodeName }}</div>
    <div class="nodeContent" v-for="(item, index) in node.plugins" :key="index">
      <div class="nodePlugin">{{ index + 1 }}: {{ item.pluginName }}</div>
    </div>
  </div >
</template>
<script>
import bus from '@/utils/event-bus'
export default {
  name: 'FlowNode',
  components: {
  },
  props: {
  },
  data () {
    return {
      node: {
        nodeId: undefined,
        nodeName: undefined,
        nodeType: undefined,
        plugins: []
      },
      flowNodeId: undefined
    }
  },
  computed: {

  },
  watch: {
    node: {
      deep: true,
      immediate: true, // immediate选项可以开启首次赋值监听
      async handler (newData, oldData) {
        // bus.$emit('changeNodeData' + this.flowNodeId, newData)
      }
    }
  },
  created () {

  },
  mounted () {
    this.$nextTick(() => {
      const id = this.$el.parentElement.parentElement.id
      this.flowNodeId = id.slice(5)
      bus.$off('sendNodeData-' + this.flowNodeId)
      bus.$on('sendNodeData-' + this.flowNodeId, (data) => {
        if (data) {
          this.node = data.dspNode
          console.log('data', data)
        }
      })
      bus.$emit('getNodeData-' + this.flowNodeId, this.flowNodeId)
    })
  },
  beforeDestroy () {
    bus.$off('sendNodeData-' + this.flowNodeId)
  },
  destroy () {
    bus.$off('sendNodeData-' + this.flowNodeId)
  },
  methods: {
    onInputChange (item, index) {
      console.log(item, index)
      this.node.plugins[index] = item
    },
    drop (event) {
      event.preventDefault()
      const pluginStr = event.dataTransfer.getData('plugin')
      const plugin = JSON.parse(pluginStr)
      if (plugin.pluginType) {
        const type = plugin.pluginType
        console.log('ttttttttttt', type)
        console.log('this.node.nodeType.code', this.node.nodeType.code)
        console.log('this.node.nodeType.code', this.node)
        if (type.code === 1 && this.node.nodeType.code === 1) {
          if (this.node.plugins.length >= 1) {
            this.$message.error(this.$t('flowNode.readerNodeWarn'))
          } else {
            this.node.plugins.push(plugin)
            bus.$emit('pluginAddToNode-' + this.flowNodeId, this.flowNodeId)
            this.$message.success(this.$t('flowNode.plugin') + plugin.pluginName + this.$t('flowNode.addSuccessfully') + this.node.nodeName)
          }
        } else if (type.code === 3 && this.node.nodeType.code === 3) {
          if (this.node.plugins.length >= 8) {
            this.$message.error(this.$t('flowNode.processNodeWarn'))
            return
          }
          this.node.plugins.push(plugin)
          bus.$emit('pluginAddToNode-' + this.flowNodeId, this.flowNodeId)
          this.$message.success(this.$t('flowNode.plugin') + plugin.pluginName + this.$t('flowNode.addSuccessfully') + this.node.nodeName)
        } else if (type.code === 2 && this.node.nodeType.code === 2) {
          if (this.node.plugins.length >= 3) {
            this.$message.error(this.$t('flowNode.writerNodeWarn'))
            return
          }
          this.node.plugins.push(plugin)
          bus.$emit('pluginAddToNode-' + this.flowNodeId, this.flowNodeId)
          this.$message.success(this.$t('flowNode.plugin') + plugin.pluginName + this.$t('flowNode.addSuccessfully') + this.node.nodeName)
        } else if (type.code === 4 && this.node.nodeType.code === 5) {
          this.node.plugins.push(plugin)
          bus.$emit('pluginAddToNode-' + this.flowNodeId, this.flowNodeId)
          this.$message.success(this.$t('flowNode.plugin') + plugin.pluginName + this.$t('flowNode.addSuccessfully') + this.node.nodeName)
        } else if (this.node.nodeType.code === 4) {
          this.$message.warn(this.$t('flowNode.unionNodeWarn'))
        } else {
          this.$message.error(this.$t('flowNode.unmatchPluginWarn') + this.node.nodeType.type)
        }
      } else {
        this.$message.error(this.$t('flowNode.unknownPluginWarn'))
      }
    },
    allowDrop (event) {
      event.preventDefault()
    }
  }
}
</script>

<style lang="less" scoped>
 .FlowNodeDiv {
     width: 100%;
     height: 100%;
     min-width: 160px;
     background-color: rgba(255, 255, 255, 0);
     padding-top: 5px;
     padding-left: 15px;
     padding-right: 15px;
     padding-bottom: 5px;
 }

 .nodeTitle {
   height: 30px;
   font-size: 10;
   text-align: center;
   overflow: hidden;
   display:flex;
   line-height: 32px;
   font-weight: 600
 }
  .nodeContent {
   border: 1px;
   border-color: rgb(255, 255, 255);
   max-height: 300px;
   margin: 3px 3px 3px 3px;
   text-align: left;
 }
 .nodePlugin {
  height: 24px;
  line-height: 24px;
  // font-weight: 500;
 }
</style>
