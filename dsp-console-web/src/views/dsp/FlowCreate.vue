<template>
  <div
    id="page-wrapper"
    class="page-wrapper"
    :style="{
      overflow: 'hidden',
      position: 'relative',
    }">
    <a-page-header
      :ghost="false"
      title="Dsp Flow"
      :style="{
        zIndex: 5,
        overflow: 'hidden',
        position: 'inline',
        boxShadow: '5px 0px 5px black;',
        borderBottom: '1px solid #e4e5e6',
        borderTop: '1px solid #e4e5e6'
      }">
      <a-form-model layout="inline" slot="extra">
        <a-form-model-item
          :label="$t('flow.flowName')">
          <a-input
            v-model="flow.flowName"
            :placeholder="$t('flowCreate.flowNameTip')" />
        </a-form-model-item>
        <a-form-model-item
          :label="$t('flow.remarkMsg')">
          <a-input
            v-model="flow.remarkMsg"
            :placeholder="$t('flowCreate.flowRemarkTip')" />
        </a-form-model-item>
        <a-form-model-item>
          <a-button type="primary" @click="handleSubmit">{{ $t('form.save') }}</a-button>
        </a-form-model-item>
        <a-form-model-item>
          <a-button type="primary" @click="clear">{{ $t('form.reset') }}</a-button>
        </a-form-model-item>
      </a-form-model>
    </a-page-header>
    <div id="flow-wrapper" class="flow-wrapper" >
      <div id="drawflow" @drop="drop" @dragover="allowDrop">
        <div class="nodes" :style="{width: '200px'}">
          <ul v-show="true">
            <li v-for="nodeItem in Object.values(nodesTemplates)" :key="nodeItem.key" :draggable="true" :data-node="nodeItem.key" @dragstart="dragNode">
              <div class="node" :style="`background: ${nodeItem.color}`" >{{ nodeItem.key }}</div>
            </li>
          </ul>
        </div>
        <div style="position: 'relative';marginLeft: 0px; marginTop: 18%;marginBottom: 24%">
          <a-slider
            @change="zoomSliderChange"
            vertical
            :default-value="5"
            :max="10"
            :autoFocus="false"
            :value="zoomSize"/>
        </div>
      </div>
      <a-button v-show="!showPluginCollapsed" class="btnDrawer btnDrawerLeft" @click="toggleCollapsed('plugin')" icon="double-left" />
      <a-drawer
        title="Dsp Plugins"
        placement="right"
        :closable="false"
        :visible="showPluginCollapsed"
        :destroyOnClose="true"
        :mask="false"
        :zIndex="4"
        :get-container="false"
        :wrap-style="{ position: 'absolute'}"
      >
        <a-button class="btnDrawer btnDrawerRight" @click="toggleCollapsed('plugin')" icon="double-right" />
        <a-collapse default-active-key="1" id="plugin-collapse" :bordered="false">
          <template #expandIcon="props">
            <a-icon type="caret-right" :rotate="props.isActive ? 90 : 0" />
          </template>
          <a-collapse-panel key="1" header="Reader Plugin" :showArrow="true" style="width: 100%; font-weight: 600;padding-left: 5px; padding-right: 5px;margin-bottom: 5px;">
            <div v-for="(item, index) in plugins.inputPlugins" :key="item.pluginName" :data-plugin="JSON.stringify(item)" draggable="true" @dragstart="dragPlugin">
              <div v-if="item.pluginType.code === 1" class="plugin-item-reader">{{ index + 1 }}. {{ item.pluginName }}</div>
              <a-divider v-if="plugins.inputPlugins.length !== 1 && index !== plugins.inputPlugins.length - 1" style="margin: 5px 0px 5px 0px"/>
            </div>
          </a-collapse-panel>
          <a-collapse-panel key="2" header="Process Plugin" :showArrow="true" style="width: 100%;font-weight: 600;padding-left: 5px; padding-right: 5px;margin-bottom: 5px">
            <div v-for="(item, index) in plugins.processPlugins" :key="item.pluginName" :data-plugin="JSON.stringify(item)" draggable="true" @dragstart="dragPlugin">
              <div v-if="item.pluginType.code === 3" class="plugin-item-process">{{ index + 1 }}. {{ item.pluginName }}</div>
              <a-divider v-if="plugins.processPlugins.length !== 1 && index !== plugins.processPlugins.length - 1" style="margin: 5px 0px 5px 0px"/>
            </div>
          </a-collapse-panel>
          <a-collapse-panel key="3" header="Writer Plugin" :showArrow="true" style="width: 100%; font-weight: 600;padding-left: 5px; padding-right: 5px;margin-bottom: 5px">
            <div v-for="(item, index) in plugins.outputPlugins" :key="item.pluginName" :data-plugin="JSON.stringify(item)" draggable="true" @dragstart="dragPlugin">
              <div v-if="item.pluginType.code === 2" class="plugin-item-writer">{{ index + 1 }}. {{ item.pluginName }}</div>
              <a-divider v-if="plugins.outputPlugins.length !== 1 && index !== plugins.outputPlugins.length - 1" style="margin: 5px 0px 5px 0px"/>
            </div>
          </a-collapse-panel>
          <a-collapse-panel key="4" header="Split Plugin" :showArrow="true" style="width: 100%; font-weight: 600;padding-left: 5px; padding-right: 5px;margin-bottom: 5px;">
            <div v-for="(item, index) in plugins.splitPlugins" :key="item.pluginName" :data-plugin="JSON.stringify(item)" draggable="true" @dragstart="dragPlugin">
              <div v-if="item.pluginType.code === 4" class="plugin-item-split">{{ index + 1 }}. {{ item.pluginName }}</div>
              <a-divider v-if="plugins.splitPlugins.length !== 1 && index !== plugins.splitPlugins.length - 1" style="margin: 5px 0px 5px 0px"/>
            </div>
          </a-collapse-panel>
        </a-collapse>
      </a-drawer>
      <a-drawer
        :title="$t('flowCreate.nodeConfig')"
        placement="bottom"
        :closable="false"
        :visible="showNodeSelectedColapsed"
        :destroyOnClose="true"
        :mask="false"
        :get-container="false"
        :height="400"
        :zIndex="6"
        :wrap-style="{ position: 'absolute'}"
      >
        <a-row v-if="Object.keys(selectedInfo.node)">
          <a-tabs default-active-key="base" type="editable-card" :animated="true" :hideAdd="true" @edit="removeNodePlugin">
            <a-tab-pane key="base" :closable="false">
              <span slot="tab">
                <a-icon type="control" />
                {{ $t('flowCreate.nodeSettings') }}
              </span>
              <!-- {{ selectedInfo.node }} -->
              <a-row :gutter="[8,8]">
                <a-col :span="8">
                  <a-card
                    style="margin-left: 5px; margin-right: 5px"
                    :title="$t('flowCreate.basicInfo')"
                    :bordered="true"
                    :bodyStyle="{padding: '5px 15px 0px 15px', 'height': '230px'}"
                    :headStyle="{'min-height': '40px'}"
                    size="small">
                    <a-form layout="horizontal" labelAlign="left" >
                      <a-form-item
                        :label="$t('flowCreate.nodeName')"
                        :label-col="{ span: 5 }"
                        :wrapper-col="{ span: 19 }"
                        style="margin-bottom: 15px"
                        required
                      >
                        <a-input v-model="selectedInfo.node.nodeName" @change="handleNodeNameChange" :placeholder="$t('flowCreate.nodeNameTip')" />
                      </a-form-item>
                      <a-form-item
                        :label="$t('flowCreate.nodeDesc')"
                        :label-col="{ span: 5 }"
                        :wrapper-col="{ span: 19 }"
                        style="margin-bottom: 15px"
                      >
                        <a-input type="textarea" :rows="5" v-model="selectedInfo.node.nodeDescription" :placeholder="$t('flowCreate.flowRemarkTip')" />
                      </a-form-item>
                    </a-form>
                  </a-card>
                </a-col>
                <a-col :span="8">
                  <a-card style="margin-left: 5px; margin-right: 5px" :title="$t('flowCreate.nodeSpeed')" :bordered="true" :bodyStyle="{padding: '5px 15px 0px 15px', 'height': '230px'}" size="small" >
                    <span slot="extra">
                      <a-switch v-model="selectedInfo.node.speedLimit.enableLimiter" />
                    </span>
                    <a-form layout="horizontal" labelAlign="left">
                      <a-form-item
                        :label-col="{span: 5}"
                        :wrapper-col="{span: 19}"
                        :label="$t('appCreate.readSpeed')"
                        style="margin-bottom: 15px"
                      >
                        <a-input-number id="readSpeed" style="width: 100%" :disabled="!selectedInfo.node.speedLimit.enableLimiter" v-model="selectedInfo.node.speedLimit.readSpeed"></a-input-number>
                      </a-form-item>
                      <a-form-item
                        :label-col="{span: 5}"
                        :wrapper-col="{span: 19}"
                        :label="$t('appCreate.processSpeed')"
                        style="margin-bottom: 15px"
                      >
                        <a-input-number id="processSpeed" style="width: 100%" :disabled="!selectedInfo.node.speedLimit.enableLimiter" v-model="selectedInfo.node.speedLimit.processSpeed"></a-input-number>
                      </a-form-item>
                      <a-form-item
                        :label-col="{span: 5}"
                        :wrapper-col="{span: 19}"
                        :label="$t('appCreate.writeSpeed')"
                        style="margin-bottom: 15px"
                      >
                        <a-input-number id="writeSpeed" style="width: 100%" :disabled="!selectedInfo.node.speedLimit.enableLimiter" v-model="selectedInfo.node.speedLimit.writeSpeed"></a-input-number>
                      </a-form-item>
                      <a-form-item
                        :label-col="{span: 5}"
                        :wrapper-col="{span: 19}"
                        :label="$t('appCreate.timeUnit')"
                        style="margin-bottom: 15px"
                      >
                        <a-input-number id="samplingInterval" style="width: 100%" :disabled="!selectedInfo.node.speedLimit.enableLimiter" v-model="selectedInfo.node.speedLimit.samplingInterval"></a-input-number>
                      </a-form-item>
                    </a-form>
                  </a-card>
                </a-col>
                <a-col :span="8">
                  <a-card style="margin-left: 5px; margin-right: 5px" :title="$t('flowCreate.nodeUnresolvedCollector')" :bordered="true" :bodyStyle="{padding: '5px 15px 0px 15px', 'height': '230px'}" size="small" >
                    <span slot="extra">
                      <a-switch v-model="selectedInfo.node.unresolvedCollectorConfig.enableCollector" />
                    </span>
                    <a-form layout="horizontal" labelAlign="left">
                      <a-form-item
                        :label-col="{span: 5}"
                        :wrapper-col="{span: 19}"
                        :label="$t('appCreate.timeUnit')"
                        style="margin-bottom: 8px"
                      >
                        <a-input-number id="samplingInterval" style="width: 100%" :disabled="!selectedInfo.node.unresolvedCollectorConfig.enableCollector" v-model="selectedInfo.node.unresolvedCollectorConfig.samplingInterval"></a-input-number>
                      </a-form-item>
                      <a-form-item
                        :label-col="{span: 5}"
                        :wrapper-col="{span: 19}"
                        :label="$t('appCreate.maxAmount')"
                        style="margin-bottom: 8px"
                      >
                        <a-input-number id="maxSamplingRecord" style="width: 100%" :disabled="!selectedInfo.node.unresolvedCollectorConfig.enableCollector" v-model="selectedInfo.node.unresolvedCollectorConfig.maxSamplingRecord"></a-input-number>
                      </a-form-item>
                    </a-form>
                  </a-card>
                </a-col>
              </a-row>
            </a-tab-pane>
            <a-tab-pane :key="selectedInfo.id + '|' + (index)" v-for="(plugin, index) in selectedInfo.node.plugins" :closable="true">
              <span slot="tab">
                <a-icon type="android" />
                {{ index + 1 }}. {{ plugin.pluginName }}
              </span>
              <!-- {{ plugin }} -->

              <a-row :gutter="[12,12]">
                <a-col :span="12">
                  <a-card
                    style="margin-left: 5px; margin-right: 5px"
                    :title="$t('flowCreate.pluginConfigs')"
                    :bordered="true"
                    :bodyStyle="{padding: '5px 15px 0px 15px', 'height': '230px', 'overflow-y': 'scroll'}"
                    :headStyle="{'min-height': '40px'}"
                    size="small" >
                    <a-form layout="vertical">
                      <a-form-item
                        :label-col="{}"
                        :wrapper-col="{}"
                        style="margin-bottom: 0px"
                      >
                        <PluginConfigs :value="selectedInfo.node.plugins[index].pluginConfigs" @change="handlepluginConfigsChange($event, index)"></PluginConfigs>
                      </a-form-item>
                    </a-form>
                  </a-card>
                </a-col>
                <a-col :span="12">
                  <a-card
                    style="margin-left: 5px; margin-right: 5px"
                    :title="$t('flowCreate.pluginInfo')"
                    :bordered="true"
                    :bodyStyle="{padding: '5px 10px 5px 10px', 'height': '230px'}"
                    :headStyle="{'min-height': '40px'}"
                    size="small">
                    <a-descriptions :column="2">
                      <a-descriptions-item :label="$t('plugin.pluginId')">
                        {{ plugin.pluginId }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.pluginJarId')">
                        {{ plugin.pluginJarId }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.pluginName')">
                        {{ plugin.pluginName }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.pluginType')">
                        <a-tag v-if="plugin.pluginType.code == 1" class="input-plugin-tag">{{ $t('plugin.pluginTypes.input') }}</a-tag>
                        <a-tag v-if="plugin.pluginType.code == 2" class="output-plugin-tag">{{ $t('plugin.pluginTypes.output') }}</a-tag>
                        <a-tag v-if="plugin.pluginType.code == 3" class="process-plugin-tag">{{ $t('plugin.pluginTypes.process') }}</a-tag>
                        <a-tag v-if="plugin.pluginType.code == 4" class="split-plugin-tag">{{ $t('plugin.pluginTypes.split') }}</a-tag>
                        <a-tag v-if="plugin.pluginType.code == 5" class="union-plugin-tag">{{ $t('plugin.pluginTypes.union') }}</a-tag>
                        <a-tag v-if="plugin.pluginType.code == 6" class="system-plugin-tag">{{ $t('plugin.pluginTypes.system') }}</a-tag>
                        <a-tag v-if="plugin.pluginType.code == 7" class="unknown-plugin-tag">{{ $t('plugin.pluginTypes.unknown') }}</a-tag>
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.pluginClass')">
                        {{ plugin.pluginClass }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.pluginDescription')">
                        {{ plugin.pluginDescription }}
                      </a-descriptions-item>
                    </a-descriptions>
                  </a-card>
                </a-col>
              </a-row>
            </a-tab-pane>
          </a-tabs>
        </a-row>
        <a-row>
        </a-row>
      </a-drawer>
    </div>
  </div>
</template>

<script>

import $ from 'jquery'
import Drawflow from 'drawflow'
import styleDrawflow from 'drawflow/dist/drawflow.min.css'
import Vue from 'vue'
import AFormItem from 'ant-design-vue/es/form/FormItem'
import FlowNode from '@/components/FlowNode'
import bus from '@/utils/event-bus'
import * as pluginApi from '@/api/plugin'
import * as flowApi from '@/api/flow'
import ConfigOptions from '@/components/ConfigOptions/ConfigOptions.vue'
import PluginConfigs from '@/components/PluginConfigs/PluginConfigs.vue'
function sleep (ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}
export default {
  name: 'Flow',
  components: {
    AFormItem,
    Drawflow,
    FlowNode,
    styleDrawflow,
    ConfigOptions,
    PluginConfigs
  },
  data () {
    return {
      nodeCollapsed: false,
      showPluginCollapsed: true,
      showNodeSelectedColapsed: false,
      zoomSize: 5,
      editor: undefined,
      dfExportData: {},
      nodesTemplates: {
        Reader: {
          key: 'Reader',
          color: '#33FFFF',
          input: 0,
          output: 1
        },
        Process: {
          key: 'Process',
          color: '#FFFF66',
          input: 1,
          output: 1
        },
        Split: {
          key: 'Split',
          color: '#33FF99',
          input: 1,
          output: 0
        },
        Union: {
          key: 'Union',
          color: '#FF33FF',
          input: 1,
          output: 1
        },
        Writer: {
          key: 'Writer',
          color: '#FFB366',
          input: 1,
          output: 0
        }
      },
      nodes: {
      },
      nodeTypes: {
        Reader: {
          type: 'Reader',
          code: 1
        },
        Writer: {
          type: 'Writer',
          code: 2
        },
        Process: {
          type: 'Process',
          code: 3
        },
        Union: {
          type: 'Union',
          code: 4
        },
        Split: {
          type: 'Split',
          code: 5
        },
        UNKNOWN: {
          type: 'Unknown',
          code: 6
        }
      },
      plugins: {
        inputPlugins: [],
        processPlugins: [],
        outputPlugins: [],
        splitPlugins: [],
        unionPlugins: [],
        systemPlugins: [],
        unknownPlugins: []
      },
      selectedInfo: {
        node: {
          nodeId: undefined,
          nodeName: undefined,
          nodeType: undefined,
          plugins: [],
          nodeDependents: [],
          nodeOutputDependents: [],
          nodeDescription: '',
          nodeInfo: {},
          speedLimit: {
            enableLimiter: false,
            readSpeed: 999999,
            processSpeed: 999999,
            writeSpeed: 999999,
            samplingInterval: 30000
          },
          unresolvedCollectorConfig: {
            enableCollector: false,
            collectorHandler: {},
            handlerConfigs: {},
            maxSamplingRecord: 999999,
            samplingInterval: 999999
          }
        },
        id: undefined
      },
      flow: {
        id: undefined,
        flowId: undefined,
        flowName: undefined,
        disableMark: 0,
        applicationRefs: [],
        pluginRefs: [],
        createUser: undefined,
        createTime: undefined,
        flowConfig: {
          nodes: {}
        },
        flowContent: {},
        remarkMsg: undefined
      },
      isUpdate: false
    }
  },
  created () {
    if (this.$route.query) {
      const record = this.$route.query.record
      if (!record) {
        console.log('is new flow create')
      } else {
        this.isUpdate = true
        this.flow = record
        // 节点映射
        this.nodes = this.flow.flowConfig.nodes
        Object.keys(this.nodes).forEach(id => {
          this.removeNodeBusListener(id)
        })
        Object.keys(this.nodes).forEach(id => {
          this.registerGetNodeDataListener(id)
          // 监听节点数据变更事件,暂时没生效
          this.registerChangeNodeDataListener(id)
          this.registerPluginAddToNodeListener(id)
          // 节点插件删除事件, 主要是监听split节点的删除
          this.registerNodePluginRemoveListener(id)
        })
      }
    }
  },
  mounted () {
    this.pluginList()
    const id = document.getElementById('drawflow')
    this.editor = new Drawflow(id, Vue, this)
    // 主动重新路由
    this.editor.reroute = true
    // 使用uuid作为图的节点ID
    this.editor.useuuid = true
    // 修复添加点
    this.editor.reroute_fix_curvature = true
    // this.editor.editor_mode = 'view' 锁定布局, 可选 edit fixed view
    this.editor.zoom_max = 1.5
    this.editor.zoom_min = 0.5
    this.editor.start()
    // 注册节点, 以供复用
    this.editor.registerNode('Reader', FlowNode, {}, {})
    this.editor.registerNode('Process', FlowNode, {}, {})
    this.editor.registerNode('Writer', FlowNode, {}, {})
    this.editor.registerNode('Split', FlowNode, {}, {})
    this.editor.registerNode('Union', FlowNode, {}, {})
    this.editor.on('zoom', (zoomLevel) => {
      const level = Math.round(zoomLevel * 10)
      console.log('zoom level', level)
      this.zoomSize = level - 5
    })
    // 监听节点选中事件
    this.editor.on('nodeSelected', (id) => {
      this.showNodeSelectedColapsed = true
      const node = this.nodes[id]
      this.selectedInfo.node = node
      this.selectedInfo.id = id
      console.log('select node data', this.selectedInfo)
    })
    // 监听节点取消选中事件
    this.editor.on('nodeUnselected', () => {
    //   this.selectedInfo.node = {}
      this.showNodeSelectedColapsed = false
    })
    // 监听节点删除事件
    this.editor.on('nodeRemoved', (id) => {
      console.log('nodeRemoved', id)
      this.removeNodeBusListener(id)
      delete this.nodes[id]
      this.showNodeSelectedColapsed = false
    })
    // 监听节点创建事件
    this.editor.on('nodeCreated', (id) => {
      const flowNode = this.editor.getNodeFromId(id)
      this.registerGetNodeDataListener(id)
      // 监听节点数据变更事件,暂时没生效
      this.registerChangeNodeDataListener(id)
      // 获取节点ID(每种类型的节点ID自增保证不重复,不保证ID连续)
      const nodeId = id
      // 创建节点数据
      const dspNode = {
        nodeId: nodeId,
        nodeName: nodeId,
        nodeType: this.nodeTypes[flowNode.name],
        plugins: [],
        nodeInfo: {
          posX: flowNode.pos_x,
          posY: flowNode.pos_y
        },
        nodeDependents: [],
        nodeOutputDependents: [],
        nodeDescription: '',
        speedLimit: {
          enableLimiter: false,
          readSpeed: 999999,
          processSpeed: 999999,
          writeSpeed: 999999,
          samplingInterval: 30000
        },
        unresolvedCollectorConfig: {
          enableCollector: false,
          collectorHandler: {},
          handlerConfigs: {},
          maxSamplingRecord: 999999,
          samplingInterval: 999999
        }
      }
      // 保存数据到nodes
      this.nodes[nodeId] = dspNode
      // 节点创建后,需要把数据发送到节点内部
      bus.$emit('sendNodeData-' + id, { dpsNode: dspNode })
      // 监听插件添加事件
      this.registerPluginAddToNodeListener(id)
      // 节点插件删除事件, 主要是监听split节点的删除
      this.registerNodePluginRemoveListener(id)
    })

    this.editor.on('connectionCreated', (connectionInfo) => {
      console.log('connectionInfo: ', connectionInfo)
      // 如果创建了链接, 就把链接的数据更新到节点上就行了
      const inputId = connectionInfo.input_id
      const outputId = connectionInfo.output_id
      const inputClass = connectionInfo.input_class
      const outputClass = connectionInfo.output_class
      const dfInputNode = this.editor.getNodeFromId(inputId)
      const dfOutputNode = this.editor.getNodeFromId(outputId)
      // 处理输出线
      console.log('dfoutputNode:', dfOutputNode)
      console.log('dfInputNode:', dfInputNode)
      console.log('dfoutputNode.outputs[outputClass]', dfOutputNode.outputs[outputClass])
      console.log('dfInputNode.inputs[inputClass]', dfInputNode.inputs[inputClass])
      if (dfOutputNode.name !== 'Split') {
        if (dfOutputNode.outputs[outputClass]['connections'].length !== 1) {
          var outputClassIndexStr = outputClass.split('_')[1]
          const outputClassIndex = parseInt(outputClassIndexStr) - 1
          const oldConnLine = dfOutputNode.outputs[outputClass]['connections'][outputClassIndex]
          this.editor.removeSingleConnection(outputId, oldConnLine.node, outputClass, oldConnLine.output)
        }
      } else {
        if (dfOutputNode.outputs[outputClass]['connections'].length !== 1) {
          const oldConnLine = dfOutputNode.outputs[outputClass]['connections'][0]
          this.editor.removeSingleConnection(outputId, oldConnLine.node, outputClass, oldConnLine.output)
        }
      }
      // 处理输入线
      if (dfInputNode.name !== 'Union') {
        if (dfInputNode.inputs[inputClass]['connections'].length !== 1) {
          var inputClassIndexStr = inputClass.split('_')[1]
          const inputClassIndex = parseInt(inputClassIndexStr) - 1
          const oldConnLine = dfInputNode.inputs[inputClass]['connections'][inputClassIndex]
          this.editor.removeSingleConnection(oldConnLine.node, inputId, oldConnLine.input, inputClass)
        }
      }
    })
    this.editor.on('connectionRemoved', (connectionInfo) => {
      const inputId = connectionInfo.input_id
      const outputId = connectionInfo.output_id
      const dspInputNode = this.nodes[inputId]
      const dspOutputNode = this.nodes[outputId]
      console.log('connectionRemoved dspInputNode', dspInputNode)
      console.log('connectionRemoved dspOutputNode', dspOutputNode)
    })
    if (this.isUpdate) {
      const drawflow = {
        drawflow: {
          Home: {
            data: this.flow.flowContent
          }
        }
      }
      this.editor.import(drawflow)
      this.refreshFlowLayout()
    }
  },
  destroyed () {
    Object.keys(this.nodes).forEach(id => {
      this.removeNodeBusListener(id)
    })
    this.editor.clear()
    this.nodes = []
    this.flow = undefined
  },
  beforeDestroy () {
    Object.keys(this.nodes).forEach(key => {
      this.removeNodeBusListener(key)
    })
  },
  methods: {
    // 注册节点获取数据监听
    registerGetNodeDataListener (id) {
      bus.$on('getNodeData-' + id, (nodeId) => {
        const dspNode = this.nodes[id]
        bus.$emit('sendNodeData-' + nodeId, { dspNode: dspNode })
      })
    },
    // 注册节点数据变更监听
    registerChangeNodeDataListener (id) {
      bus.$on('changeNodeData-' + id, (data) => {
        console.log('节点内修改数据， emit到节点外', data)
      })
    },
    // 注册插件添加到节点监听
    registerPluginAddToNodeListener (id) {
      bus.$on('pluginAddToNode-' + id, (id) => {
        let nodeInfo
        try {
          nodeInfo = this.editor.getNodeFromId(id)
        } catch (err) {
          console.error(err.message)
        }
        // 如果是split节点添加了插件, 需要添加一个output
        if (nodeInfo.name === 'Split') {
          this.editor.addNodeOutput(id, nodeInfo.output + 1)
        }
        // 节点添加插件后刷新节点,防止图上线错位
        setTimeout(() => {
          this.editor.updateConnectionNodes('node-' + id)
        }, 50)
      })
    },
    // 注册节点插件移除监听
    registerNodePluginRemoveListener (id) {
      bus.$on('nodePluginRemove-' + id, (pluginInfo) => {
        let dfOutputNode
        try {
          dfOutputNode = this.editor.getNodeFromId(pluginInfo.id)
        } catch (err) {
          console.error(err.message)
        }
        if (dfOutputNode.name === 'Split') {
          const outputClass = 'output_' + (parseInt(pluginInfo.index) + 1)
          console.log('outputClass', outputClass)
          const outputConns = dfOutputNode.outputs[outputClass]['connections']
          if (outputConns.length !== 0) {
            outputConns.forEach(item => {
              console.log('item:', item)
              this.editor.removeSingleConnection(dfOutputNode.id, item.node, outputClass, item.output)
            })
          }
          this.editor.removeNodeOutput(dfOutputNode.id, outputClass)
        }
        setTimeout(() => {
          this.editor.updateConnectionNodes('node-' + dfOutputNode.id)
        }, 50)
        console.log('node plugin remove', dfOutputNode)
      })
    },
    // 刷新流程图布局
    async refreshFlowLayout () {
      await sleep(100)
      Object.values(this.nodes).forEach(node => {
        this.editor.updateConnectionNodes('node-' + node.nodeId)
      })
    },
    // 删除一个节点的所有事件监听
    removeNodeBusListener (nodeId) {
      bus.$off('nodePluginRemove-' + nodeId, {})
      bus.$off('pluginAddToNode-' + nodeId, {})
      bus.$off('getNodeData-' + nodeId, {})
      bus.$off('changeNodeData-' + nodeId, {})
      bus.$off('sendNodeData-' + nodeId, {})
      bus.$off('nodePluginRemove-' + nodeId)
      bus.$off('pluginAddToNode-' + nodeId)
      bus.$off('getNodeData-' + nodeId)
      bus.$off('changeNodeData-' + nodeId)
      bus.$off('sendNodeData-' + nodeId)
    },
    pluginList () {
      pluginApi.plugins().then(r => {
        if (r.success) {
          this.plugins = r.result
        } else {
          console.log('error')
        }
      })
    },
    // 折叠面板
    toggleCollapsed (type) {
      if (type === 'node') {
        this.nodeCollapsed = !this.nodeCollapsed
      } else if (type === 'plugin') {
        this.showPluginCollapsed = !this.showPluginCollapsed
        if (this.showPluginCollapsed) {
          $('.btnDrawerLeft').hide()
        } else {
          $('.btnDrawerLeft').show()
        }
      }
    },
    // 清除
    clear () {
      this.showNodeSelectedColapsed = false
      this.editor.clear()
      this.nodes = {}
      this.selectedInfo = { node: {}, id: undefined }
    },
    // 拖动节点，需要把数据设置到event里
    dragNode (event) {
      event.dataTransfer.setData('node', event.target.getAttribute('data-node'))
    },
    // 拖动插件，需要把数据设置到event里
    dragPlugin (event) {
      event.dataTransfer.setData('plugin', event.target.getAttribute('data-plugin'))
    },
    // 拖动节点到图上得时候
    drop (event) {
      event.preventDefault()
      const nodeKey = event.dataTransfer.getData('node')
      if (nodeKey) {
        this.addNodeToDrawFlow(nodeKey, event.clientX, event.clientY)
      }
    },
    allowDrop (event) {
      event.preventDefault()
    },
    addNodeToDrawFlow (nodeKey, px, py) {
      const posX = px * (this.editor.precanvas.clientWidth / (this.editor.precanvas.clientWidth * this.editor.zoom)) - (this.editor.precanvas.getBoundingClientRect().x * (this.editor.precanvas.clientWidth / (this.editor.precanvas.clientWidth * this.editor.zoom)))
      const posY = py * (this.editor.precanvas.clientHeight / (this.editor.precanvas.clientHeight * this.editor.zoom)) - (this.editor.precanvas.getBoundingClientRect().y * (this.editor.precanvas.clientHeight / (this.editor.precanvas.clientHeight * this.editor.zoom)))
      const nodeTemplate = this.nodesTemplates[nodeKey]
      this.editor.addNode(nodeKey, nodeTemplate.input, nodeTemplate.output, posX + -70, posY - 30, nodeKey, {}, nodeKey, 'vue')
    },
    handleSubmit () {
      const exportValue = this.editor.export()
      this.dfExportData = exportValue.drawflow.Home.data
      try {
        let readerNodeExist = false
        let writerNodeExist = false
        Object.keys(this.nodes).forEach(id => {
          const dfNode = this.dfExportData[id]
          const dspNode = this.nodes[id]
          // 保存输入的线
          dspNode.nodeDependents = []
          dspNode.nodeOutputDependents = []
          Object.keys(dfNode.inputs).forEach(inputClass => {
            const inputLines = dfNode.inputs[inputClass]['connections']
            inputLines.forEach(item => {
              const inputNodeId = item.node
              if (this.nodes[inputNodeId].nodeType === 'Split') {
                const index = item.input.split('_')[1]
                const nodeDep = item.node + '.' + index
                dspNode.nodeDependents.push(nodeDep)
              } else {
                dspNode.nodeDependents.push(inputNodeId)
              }
            })
          })
          // 保存输出的线
          Object.keys(dfNode.outputs).forEach((outputClass, index) => {
            console.log('outputClass', outputClass)
            const outputLines = dfNode.outputs[outputClass]['connections']
            console.log('output conns', outputLines)
            outputLines.forEach((item) => {
              const outputNodeId = item.node
              if (dspNode.nodeType === 'Split') {
                const outputClassIndex = item.output.split('_')[1]
                console.log('arr index: ', index)
                const nodeDep = (index + 1) + '.' + item.node + '.' + outputClassIndex
                dspNode.nodeOutputDependents.push(nodeDep)
              } else {
                dspNode.nodeOutputDependents.push(outputNodeId)
              }
            })
          })
          // 检查节点配置是否正确
          if (dspNode.nodeType.code !== 4 && dspNode.plugins.length < 1) {
            this.$message.error(this.$t('flowCreate.emptyNodeWarn') + ' ' + this.$t('flowCreate.nodeType') + ': ' + dspNode.nodeType.type + '. ' + this.$t('flowCreate.nodeName') + ': ' + dspNode.nodeName)
            throw new Error('flow create check failed')
          }
          if (dspNode.nodeType.code === 1) {
            readerNodeExist = true
          }
          if (dspNode.nodeType.code === 2) {
            writerNodeExist = true
          }
        })
        if (Object.keys(this.nodes).length < 1) {
          this.$message.error(this.$t('flowCreate.zeroNodeWarn'))
          throw new Error('flow create check failed')
        }
        if (!readerNodeExist) {
          this.$message.error(this.$t('flowCreate.zeroReaderNodeWarn'))
          throw new Error('flow create check failed')
        }
        if (!this.flow.flowName) {
          this.$message.error(this.$t('flowCreate.flowNameTip'))
          throw new Error('flow create check failed')
        }
      } catch (error) {
        console.log('submit flow failed, failed msg: ', error)
        return
      }
      console.log('export data: ', this.dfExportData)
      console.log('flow==========>', this.flow)
      console.log('nodes==========>', this.nodes)
      this.flow.flowConfig.nodes = this.nodes
      this.flow.flowContent = this.dfExportData
      if (this.isUpdate) {
        flowApi.flowUpdate(this.flow).then(r => {
          if (r.success) {
            console.log('保存成功', r)
            this.$router.push({ path: '/dsp/flow' })
            setTimeout(() => {
              this.$notification.success({
                message: this.$t('message.success'),
                description: this.$t('appCreate.saveSuccess')
              })
            }, 1000)
          } else {
            this.$notification.error({
              message: this.$t('message.failed'),
              description: this.$t('appCreate.saveFailed') + ',' + r.message
            })
          }
        })
      } else {
        flowApi.flowSave(this.flow).then(r => {
          if (r.success) {
            console.log('保存成功', r)
            this.$router.push({ path: '/dsp/flow' })
            setTimeout(() => {
              this.$notification.success({
                message: this.$t('message.success'),
                description: this.$t('appCreate.saveSuccess')
              })
            }, 1000)
          } else {
            this.$notification.error({
              message: this.$t('message.failed'),
              description: this.$t('appCreate.saveFailed') + ',' + r.message
            })
          }
        })
      }
    },
    zoomSliderChange (value) {
      if (value > this.zoomSize) {
        this.editor.zoom_in()
      } else {
        this.editor.zoom_out()
      }
      this.zoomSize = value
    },
    removeNodePlugin (target, action) {
      const pluginInfo = target.split('|')
      const nodeId = pluginInfo[0]
      const pluginIndex = pluginInfo[1]
      this.selectedInfo.node.plugins.splice(pluginIndex, 1)
      bus.$emit('nodePluginRemove-' + nodeId, { id: nodeId, index: pluginIndex })
    },
    handleNodeNameChange (e) {
      setTimeout(() => {
        this.editor.updateConnectionNodes('node-' + this.selectedInfo.id)
      }, 50)
    },
    handlepluginConfigsChange (data, index) {
      Object.values(data).forEach(o => {
        if (this.selectedInfo.node.plugins[index].pluginConfigs[o.key] !== undefined) {
          console.log('存在!!!')
          this.selectedInfo.node.plugins[index].pluginConfigs[o.key] = o
        } else {
          console.log('不存在!!!!!!')
        }
      })
    }
  }

}
</script>

<style>
    #app {
    text-align: initial;
    }
  .ant-page-header {
    border-bottom: none !important;;
    box-shadow: 0 3px 8px rgb(197 198 199);
   }
  .ant-drawer-content, .ant-drawer-wrapper-body {
    overflow: initial;
  }
  .btnDrawer {
    position: absolute;
    right: 0;
    top: 45%;
    z-index: 1001;
    box-shadow: -2px 0 8px rgb(0 0 0 / 15%);
    border-right: none;
    border-radius: 5px 0 0 5px;
  }
  .btnDrawerRight {
    left: -32px;
  }
  .ant-drawer-body {
    padding: 5px 5px 5px 5px;
  }
  .page-wrapper {
    display: flex;
    flex-direction: column;
    height: 100%;
  }
  .header-wrapper {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 20px;
    background-color: white;
  }
  .operate-button {
    margin: 0 8px;
  }
  .flow-wrapper {
    flex: 1;
    position: relative;
    display: flex;
  }
  .nodes {
    width: 200px;
    height: 320px;
    background-color: rgba(255, 255, 255, 0.0);
    overflow: auto;
    position: fixed;
    z-index: 2;
    border-width:10px;
  }
  .node-collapsed-button {
    text-align: right;
  }
  .nodes ul {
    padding-inline-start: 0px;
    padding: 5px 10px;
    list-style: none;
    margin-bottom: 0;
  }
  .nodes li {
    background: transparent;
  }
  .node {
    border-radius: 8px;
    /* border: 2px solid #494949; */
    display: block;
    height:50px;
    line-height:50px;
    color: #000;
    box-shadow: 0 2px 4px rgb(0 0 0);
    /* padding: 5px 10px; */
    margin: 10px 0px;
    cursor: move;
    text-align: center;
    font-weight: bold;
  }
  .plugin {
    width: 200px;
    height: 300px;
    background-color: lightgrey;
    overflow: auto;
    right: 0;
    z-index: 10;
  }
  .plugin ul {
    padding-inline-start: 0px;
    padding: 5px 10px;
    list-style: none;
  }
  .plugin li {
    background: transparent;
  }
  .plugin-item-reader {
    border-radius: 3px;
    /* border: 1px solid #33ffff; */
        background: linear-gradient(to right, rgba(51, 255, 255, 0.5) 0%, rgba(51, 255, 255, 0.2) 50%, rgba(51, 255, 255, 0.5) 100%);
    display: block;
    height:24px;
    line-height:24px;
    /* padding: 3px 3px 3px 3px; */
    padding-left: 10px;
    margin: 5px 0px 8px 8px;
    text-align: left;
    font-weight: 600;
    box-shadow: 3px 1px 3px #888888;
  }
    .plugin-item-process {
    border-radius: 3px;
    background: linear-gradient(to right, rgba(255, 255, 102, 0.5) 0%, rgba(255, 255, 102, 0.2) 50%, rgba(255, 255, 102, 0.5) 100%);
    display: block;
    height:24px;
    line-height:24px;
    /* padding: 3px 3px 3px 3px; */
    padding-left: 10px;
    margin: 5px 0px 8px 8px;
    text-align: left;
    font-weight: 600;
    box-shadow: 3px 1px 3px #888888;
  }
    .plugin-item-writer {
    border-radius: 3px;
    /* border: 1px solid #33ffff; */
    background: linear-gradient(to right, rgba(255, 179, 102, 0.5) 0%, rgba(255, 179, 102, 0.2) 50%, rgba(255, 179, 102, 0.5) 100%);
    display: block;
    height:24px;
    line-height:24px;
    /* padding: 3px 3px 3px 3px; */
    padding-left: 10px;
    margin: 5px 0px 8px 8px;
    text-align: left;
    font-weight: 600;
    box-shadow: 3px 1px 3px #888888;
  }
    .plugin-item-split {
    border-radius: 3px;
    /* border: 1px solid #33ffff; */
    background: linear-gradient(to right, rgba(51, 255, 153, 0.5) 0%, rgba(51, 255, 153, 0.2) 50%, rgba(51, 255, 153, 0.5) 100%);
    display: block;
    height:24px;
    line-height:24px;
    /* padding: 3px 3px 3px 3px; */
    padding-left: 10px;
    margin: 5px 0px 8px 8px;
    text-align: left;
    font-weight: 600;
    box-shadow: 3px 1px 3px #888888;
  }
  .node-info {
    height: 200px;
    background-color: #fff;
  }

:root {
  --dfBackgroundColor: rgba(255, 255, 255, 0.63);
  --dfBackgroundSize: 24px;
  --dfBackgroundImage: linear-gradient(to right, rgba(0, 0, 0, 0.2) 1px, transparent 1px), linear-gradient(to bottom, rgba(0, 0, 0, 0.2) 1px, transparent 1px);

  --dfNodeType: flex;
  --dfNodeTypeFloat: none;
  --dfNodeBackgroundColor: #ffffff;
  --dfNodeTextColor: #000000;
  --dfNodeBorderSize: 0px;
  --dfNodeBorderColor: #2c2c2c;
  --dfNodeBorderRadius: 6px;
  --dfNodeMinHeight: 60px;
  --dfNodeMinWidth: 160px;
  --dfNodePaddingTop: 5px;
  --dfNodePaddingBottom: 5px;
  --dfNodeBoxShadowHL: 0px;
  --dfNodeBoxShadowVL: 1px;
  --dfNodeBoxShadowBR: 10px;
  --dfNodeBoxShadowS: 1px;
  --dfNodeBoxShadowColor: #000000;

  --dfNodeHoverBackgroundColor: #ffffff;
  --dfNodeHoverTextColor: #000000;
  --dfNodeHoverBorderSize: 2px;
  --dfNodeHoverBorderColor: #000000;
  --dfNodeHoverBorderRadius: 4px;

  --dfNodeHoverBoxShadowHL: 0px;
  --dfNodeHoverBoxShadowVL: 2px;
  --dfNodeHoverBoxShadowBR: 15px;
  --dfNodeHoverBoxShadowS: 2px;
  --dfNodeHoverBoxShadowColor: #4ea9ff;

  --dfNodeSelectedBackgroundColor: rgba(249, 215, 211, 1);
  --dfNodeSelectedTextColor: rgba(0, 0, 0, 1);
  --dfNodeSelectedBorderSize: 2px;
  --dfNodeSelectedBorderColor: #000000;
  --dfNodeSelectedBorderRadius: 4px;

  --dfNodeSelectedBoxShadowHL: 0px;
  --dfNodeSelectedBoxShadowVL: 2px;
  --dfNodeSelectedBoxShadowBR: 15px;
  --dfNodeSelectedBoxShadowS: 2px;
  --dfNodeSelectedBoxShadowColor: #4ea9ff;

  --dfInputBackgroundColor: #ffffff;
  --dfInputBorderSize: 2px;
  --dfInputBorderColor: #588a7e;
  --dfInputBorderRadius: 50px;
  --dfInputLeft: -8px;
  --dfInputHeight: 15px;
  --dfInputWidth: 15px;

  --dfInputHoverBackgroundColor: #ffffff;
  --dfInputHoverBorderSize: 2px;
  --dfInputHoverBorderColor: #000000;
  --dfInputHoverBorderRadius: 50px;

  --dfOutputBackgroundColor: #ffffff;
  --dfOutputBorderSize: 2px;
  --dfOutputBorderColor: #7c6a35;
  --dfOutputBorderRadius: 50px;
  --dfOutputRight: 8px;
  --dfOutputHeight: 15px;
  --dfOutputWidth: 15px;

  --dfOutputHoverBackgroundColor: #ffffff;
  --dfOutputHoverBorderSize: 2px;
  --dfOutputHoverBorderColor: #000000;
  --dfOutputHoverBorderRadius: 50px;

  --dfLineWidth: 5px;
  --dfLineColor: #4682b4;
  --dfLineHoverColor: #4682b4;
  --dfLineSelectedColor: #43b993;

  --dfRerouteBorderWidth: 2px;
  --dfRerouteBorderColor: #000000;
  --dfRerouteBackgroundColor: #ffffff;

  --dfRerouteHoverBorderWidth: 2px;
  --dfRerouteHoverBorderColor: #000000;
  --dfRerouteHoverBackgroundColor: #ffffff;

  --dfDeleteDisplay: block;
  --dfDeleteColor: #ffffff;
  --dfDeleteBackgroundColor: #000000;
  --dfDeleteBorderSize: 2px;
  --dfDeleteBorderColor: #ffffff;
  --dfDeleteBorderRadius: 50px;
  --dfDeleteTop: -15px;

  --dfDeleteHoverColor: #000000;
  --dfDeleteHoverBackgroundColor: #ffffff;
  --dfDeleteHoverBorderSize: 2px;
  --dfDeleteHoverBorderColor: #000000;
  --dfDeleteHoverBorderRadius: 50px;

}

  #drawflow {
    text-align: initial;
    position: relative;
    width: 100%;
    height: 100%;
    background: var(--dfBackgroundColor);
    background-size: var(--dfBackgroundSize) var(--dfBackgroundSize);
    background-image: var(--dfBackgroundImage);
  }

  .drawflow .drawflow-node {
    display: var(--dfNodeType);
    /*background: var(--dfNodeBackgroundColor);*/
    color: var(--dfNodeTextColor);
    border: var(--dfNodeBorderSize)  solid var(--dfNodeBorderColor);
    border-radius: var(--dfNodeBorderRadius);
    min-height: var(--dfNodeMinHeight);
    width: auto;
    min-width: var(--dfNodeMinWidth);
    /* padding-top: var(--dfNodePaddingTop);
    padding-bottom: var(--dfNodePaddingBottom); */
    padding-top: 0px;
    padding-bottom: 0px;
    padding-left: 0px;
    padding-right: 0px;
    -webkit-box-shadow: var(--dfNodeBoxShadowHL) var(--dfNodeBoxShadowVL) var(--dfNodeBoxShadowBR) var(--dfNodeBoxShadowS) var(--dfNodeBoxShadowColor);
    box-shadow:  var(--dfNodeBoxShadowHL) var(--dfNodeBoxShadowVL) var(--dfNodeBoxShadowBR) var(--dfNodeBoxShadowS) var(--dfNodeBoxShadowColor);
  }

    .drawflow .drawflow-node .input {
    left: var(--dfInputLeft);
    background: var(--dfInputBackgroundColor);
    border: var(--dfInputBorderSize)  solid var(--dfInputBorderColor);
    border-radius: var(--dfInputBorderRadius);
    height: var(--dfInputHeight);
    width: var(--dfInputWidth);
    }

    .drawflow .drawflow-node .input:hover {
    background: var(--dfInputHoverBackgroundColor);
    border: var(--dfInputHoverBorderSize)  solid var(--dfInputHoverBorderColor);
    border-radius: var(--dfInputHoverBorderRadius);
    }

    .drawflow .drawflow-node .outputs {
    float: var(--dfNodeTypeFloat);
    }

    .drawflow .drawflow-node .output {
    right: var(--dfOutputRight);
    background: var(--dfOutputBackgroundColor);
    border: var(--dfOutputBorderSize)  solid var(--dfOutputBorderColor);
    border-radius: var(--dfOutputBorderRadius);
    height: var(--dfOutputHeight);
    width: var(--dfOutputWidth);
    }

    .drawflow .drawflow-node .output:hover {
    background: var(--dfOutputHoverBackgroundColor);
    border: var(--dfOutputHoverBorderSize)  solid var(--dfOutputHoverBorderColor);
    border-radius: var(--dfOutputHoverBorderRadius);
    }

    .drawflow .connection .main-path {
  stroke-width: var(--dfLineWidth);
  stroke: var(--dfLineColor);
}

.drawflow .connection .main-path:hover {
  stroke: var(--dfLineHoverColor);
}

.drawflow .connection .main-path.selected {
  stroke: var(--dfLineSelectedColor);
}

.drawflow .connection .point {
  stroke: var(--dfRerouteBorderColor);
  stroke-width: var(--dfRerouteBorderWidth);
  fill: var(--dfRerouteBackgroundColor);
}

.drawflow .connection .point:hover {
  stroke: var(--dfRerouteHoverBorderColor);
  stroke-width: var(--dfRerouteHoverBorderWidth);
  fill: var(--dfRerouteHoverBackgroundColor);
}

.drawflow-delete {
  display: var(--dfDeleteDisplay);
  color: var(--dfDeleteColor);
  background: var(--dfDeleteBackgroundColor);
  border: var(--dfDeleteBorderSize) solid var(--dfDeleteBorderColor);
  border-radius: var(--dfDeleteBorderRadius);
}

.parent-node .drawflow-delete {
  top: var(--dfDeleteTop);
}

.drawflow-delete:hover {
  color: var(--dfDeleteHoverColor);
  background: var(--dfDeleteHoverBackgroundColor);
  border: var(--dfDeleteHoverBorderSize) solid var(--dfDeleteHoverBorderColor);
  border-radius: var(--dfDeleteHoverBorderRadius);
}
  .drawflow .Reader {
    background-color: #33FFFF;
  }

  .drawflow .Process {
    background-color: #FFFF66;
  }

  .drawflow .Writer {
    background-color: #FFB366;
  }

  .drawflow .Split {
    background-color: #33FF99;
  }

  .drawflow .Union {
    background-color: #FF33FF
  }

.drawflow .connection .main-path {
  stroke: #4ea9ff;
  stroke-width: 3px;
}

.drawflow .drawflow-node .input:hover, .drawflow .drawflow-node .output:hover {
  background: #4ea9ff;
}

.drawflow .connection .point.selected, .drawflow .connection .point:hover {
  fill: #4ea9ff;
}

.ant-collapse-content .ant-collapse-content-box {
    padding: 0px 4px 0px 4px;
    background: #ffffff;
}

.ant-collapse .ant-collapse-item .ant-collapse-header {
    color: rgba(0, 0, 0, 1);
}

.input-plugin-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(51, 255, 255));
  border-radius: 3%;
  color: #000;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}
.output-plugin-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(255, 179, 102));
  border-radius: 3%;
  color: #000;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}

.process-plugin-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(255, 255, 102));
  border-radius: 3%;
  font-weight: 500;
  color: #000;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}

.split-plugin-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(51, 255, 153));
  border-radius: 3%;
  color: #000;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}

.union-plugin-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(255, 51, 255));
  border-radius: 3%;
  color: #000;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}

.system-plugin-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(8, 124, 250));
  border-radius: 3%;
  color: #000;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}

.unknown-plugin-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(199, 200, 201));
  border-radius: 3%;
  color: #000;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}
</style>
