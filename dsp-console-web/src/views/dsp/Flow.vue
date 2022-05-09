<template>
  <a-card :bordered="true" title="Dsp Flows">
    <div slot="extra">
      <a-col :span="12">
        <a-upload
          name="file"
          accept=".flow"
          :transform-file="uploadFlow"
          :customRequest="() => {}"
          :showUploadList="false"
        >
          <a-button style="float:left; margin-right: 14px" type="primary" icon="upload" v-action:flow:upload>{{ $t('flow.uploadButton') }}</a-button>
        </a-upload>
      </a-col>
      <a-col :span="12">
        <router-link to="/dsp/flow/create">
          <a-button style="float:right" type="primary" icon="plus" v-action:flow:create>{{ $t('flow.createButton') }}</a-button>
        </router-link>
      </a-col>
    </div>
    <div class="page-body">
      <a-row style="margin-top:0px">
        <a-col>
          <a-table
            rowKey="id"
            :columns="columns"
            :pagination="pagination"
            :data-source="data"
            style="background-color: #fff;"
            @change="handleTableChange"
          >
            <span slot="tags" slot-scope="tags">
              <a-tag v-if="tags == 0" class="enable-tag">{{ $t('form.enable') }}</a-tag>
              <a-tag v-if="tags == 1" class="disable-tag">{{ $t('form.disable') }}</a-tag>
            </span>
            <span slot="flowRefsTags" slot-scope="text, record">
              <a-popover title="App Refs">
                <a-tag color="#1890ff" class="refs-num-tag-app">
                  {{ record.applicationRefs.length }}
                </a-tag>
                <template slot="content">
                  <li v-for="(app, index) in record.applicationRefs" :key="app.jobId" style="color: #40a9ff; font-weight: 500">
                    {{ index + 1 }}. {{ app.appName }}
                    <a-divider v-if="record.applicationRefs.length !== 1" style="margin: 3px 0px 3px 0px"/>
                  </li>
                </template>
              </a-popover>
              <a-popover title="Plugin Refs">
                <a-tag color="#1890ff" class="refs-num-tag-plugin">
                  {{ record.pluginRefs.length }}
                </a-tag>
                <template slot="content">
                  <li v-for="(plugin, index) in record.pluginRefs" :key="plugin.id" style="color: #40a9ff; font-weight: 500">
                    {{ index + 1 }}. {{ plugin.pluginName }}
                    <a-divider v-if="record.pluginRefs.length !== 1" style="margin: 3px 0px 3px 0px"/>
                  </li>
                </template>
              </a-popover>
            </span>
            <span slot="action" slot-scope="text,record">
              <a-button-group>
                <a-tooltip placement="bottom" >
                  <template slot="title">
                    <span>{{ $t('form.edit') }}</span>
                  </template>
                  <a-badge :dot="record.updateMark === 1">
                    <a-button v-action:flow:update @click="editFlow(record)" shape="circle" type="link" >
                      <b-icon-tools style="width: 18px; height: 18px;"></b-icon-tools>
                    </a-button>
                  </a-badge>
                </a-tooltip>
                <a-tooltip placement="bottom" >
                  <template slot="title">
                    <span>{{ $t('form.download') }}</span>
                  </template>
                  <a-button v-action:flow:download @click="downloadFlow(record)" shape="circle" type="link" >
                    <b-icon-cloud-download-fill style="width: 18px; height: 18px;"></b-icon-cloud-download-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('form.disable')">
                  <a-button v-if="record.disableMark==0" v-action:flow:disable @click="flowDisable(record)" shape="circle" type="link" >
                    <b-icon-x-circle-fill style="width: 18px; height: 18px;"></b-icon-x-circle-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('form.enable')">
                  <a-button v-if="record.disableMark==1" v-action:flow:disable @click="flowDisable(record)" shape="circle" type="link" >
                    <b-icon-check-circle-fill style="width: 18px; height: 18px;"></b-icon-check-circle-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" >
                  <template slot="title">
                    <span>{{ $t('form.delete') }}</span>
                  </template>
                  <a-popconfirm :title="$t('flow.deleteConfirm')" :ok-text="$t('form.deleteOk')" :cancel-text="$t('form.deleteCancel')" @confirm="deleteFlow(record)">
                    <a-button v-action:flow:delete shape="circle" type="link" >
                      <b-icon-trash-fill style="width: 18px; height: 18px;"></b-icon-trash-fill>
                    </a-button>
                  </a-popconfirm>
                </a-tooltip>
              </a-button-group>
            </span>
            <div
              slot="filterDropdown"
              slot-scope="{ setSelectedKeys, selectedKeys, confirm, clearFilters, column }"
              style="padding: 8px"
            >
              <a-input
                v-ant-ref="(c) => (searchInput = c)"
                :placeholder="$t('form.search') + ' ' + `${column.title}`"
                :value="selectedKeys[0]"
                style="width: 188px; margin-bottom: 8px; display: block"
                @change="(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])"
                @pressEnter="() => handleFlowSearch(selectedKeys, confirm, column.dataIndex)"
              />
              <a-button
                type="primary"
                icon="search"
                size="small"
                style="width: 90px; margin-right: 8px"
                @click="() => handleFlowSearch(selectedKeys, confirm, column.dataIndex)"
              >
                {{ $t('form.search') }}
              </a-button>
              <a-button size="small" style="width: 90px" @click="() => handleFlowReset(clearFilters)">
                {{ $t('form.reset') }}
              </a-button>
            </div>
            <a-icon
              slot="filterIcon"
              slot-scope="filtered"
              type="search"
              :style="{ color: filtered ? '#108ee9' : undefined }"
            />
          </a-table>
        </a-col>
      </a-row>
    </div>
  </a-card>
</template>

<script>
// import countTo from 'vue-count-to'
import * as flowApi from '@/api/flow'
import moment from 'moment'
export default {
  name: 'Flow',
  components: {
  },
  computed: {
    columns () {
      const columns = [
        {
          title: this.$t('flow.flowName'),
          dataIndex: 'flowName',
          key: 'flowName',
          align: 'center',
          width: '15%',
          scopedSlots: {
            filterDropdown: 'filterDropdown',
            filterIcon: 'filterIcon'
          }
        },
        {
          title: this.$t('flow.flowId'),
          dataIndex: 'flowId',
          key: 'flowId',
          align: 'center',
          width: '15%',
          scopedSlots: {
            filterDropdown: 'filterDropdown',
            filterIcon: 'filterIcon'
          }
        },
        {
          title: this.$t('flow.disableMark'),
          key: 'disableMark',
          dataIndex: 'disableMark',
          align: 'center',
          width: '10%',
          filters: [
            { text: this.$t('form.enable'), value: 0 },
            { text: this.$t('form.disable'), value: 1 }
          ],
          filterMultiple: false,
          scopedSlots: {
            customRender: 'tags'
          }
        },
        {
          title: this.$t('flow.createUser'),
          key: 'createUser',
          dataIndex: 'createUser',
          align: 'center',
          width: '10%'
        },
        {
          title: this.$t('flow.flowRefs'),
          key: 'flowRefs',
          dataIndex: 'flowRefs',
          align: 'center',
          width: '10%',
          scopedSlots: {
            customRender: 'flowRefsTags'
          }
        },
        {
          title: this.$t('flow.createTime'),
          key: 'createTime',
          dataIndex: 'createTime',
          sorter: true,
          align: 'center',
          width: '10%',
          customRender: (text, row, index) => {
            return moment(text).format('YYYY-MM-DD HH:mm:ss')
          }
        },
        {
          title: this.$t('flow.remarkMsg'),
          key: 'remarkMsg',
          dataIndex: 'remarkMsg',
          align: 'center',
          width: '15%',
          ellipsis: true
        },
        {
          title: this.$t('flow.action'),
          key: 'action',
          align: 'center',
          width: '15%',
          scopedSlots: {
            customRender: 'action'
          }
        }
      ]
      return columns
    }
  },
  data () {
    return {
      flowQuery: {
        flowId: undefined,
        flowName: undefined,
        disableMark: undefined,
        sortField: 'createTime',
        sortOrder: 'descend',
        pageNo: 1,
        pageSize: 10
      },
      pagination: {
        total: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100']
      },
      startVal: 0,
      endVal: 3000,
      data: []
    }
  },
  created () {
    this.flows(this.flowQuery)
  },
  mounted () {
  },
  methods: {
    flows (params) {
      flowApi.flowList(params).then(r => {
        if (r.success) {
          this.data = r.result.data
          console.log(this.data)
          this.pagination.total = r.result.totalCount
          this.pagination.current = r.result.pageNo
          this.pagination.pageSize = r.result.pageSize
        } else {
          console.log('error')
        }
      })
    },
    deleteFlow (data) {
      console.log('deletedddddd', data)
      flowApi.deleteFlow(data).then(r => {
        console.log('response: ', r)
        if (r.success) {
          this.$notification.success({
            message: this.$t('message.success'),
            description: this.$t('message.flow.delete_success')
          })
          setTimeout(() => {
            this.flows(this.flowQuery)
          }, 1000)
        } else {
          this.$notification.error({
            message: this.$t('message.failed'),
            description: r.message || this.$t('message.flow.delete_failed')
          })
        }
      })
    },
    uploadFlow (file) {
      const reader = new FileReader()
      const that = this
      reader.onload = function (event) {
        console.log('event.target.result', event.target.result)
        const fileContent = event.target.result
        if (fileContent !== undefined) {
          const flow = JSON.parse(fileContent)
          flowApi.flowUpload(flow).then(r => {
            console.log('response: ', r)
            if (r.success) {
              that.$notification.success({
                message: that.$t('message.success'),
                description: that.$t('message.flow.upload_success')
              })
              setTimeout(() => {
                that.flows(that.flowQuery)
              }, 1000)
            } else {
              that.$notification.error({
                message: that.$t('message.success'),
                description: r.message || that.$t('message.flow.upload_failed') + ',' + r.message,
                duration: 5
              })
            }
          })
        }
      }
      reader.readAsText(file)
      console.log(file)
      console.log('upload flow start')
    },
    editFlow (data) {
      console.log(data)
      this.$router.push({ path: '/dsp/flow/create', query: { record: data } })
    },
    downloadFlow (record) {
      console.log(record)
      const params = { flowId: record.id }
      flowApi.flowDownload(params).then(r => {
        if (r.type === 'application/octet-stream') {
          console.log('response: ', r)
          const reader = new FileReader()
          reader.readAsDataURL(r)
          reader.onload = (e) => {
            const a = document.createElement('a')
            a.download = record.flowName + '.flow'
            a.href = e.target.result
            document.body.appendChild(a)
            a.click()
            document.body.removeChild(a)
          }
          this.$notification['success']({
            message: this.$t('message.success'),
            description: 'flow文件下载成功',
            duration: 5
          })
        } else {
          this.$notification['error']({
            message: this.$t('message.failed'),
            description: 'flow文件下载失败',
            duration: 5
          })
        }
      })
    },
    flowDisable (record) {
      console.log('flow id : ', record.id)
      flowApi.disableFlow(record).then(r => {
        console.log('response: ', r)
        if (r.success) {
          record.disableMark = record.disableMark === 0 ? 1 : 0
          this.$notification['success']({
            message: this.$t('message.success'),
            description: record.disableMark === 1 ? this.$t('message.flow.disable') : this.$t('message.flow.enable'),
            duration: 5
          })
        } else {
          this.$notification['error']({
            message: this.$t('message.failed'),
            description: r.message || '',
            duration: 5
          })
        }
      })
    },
    handleFlowSearch (selectedKeys, confirm, dataIndex) {
      confirm()
    },
    handleFlowReset (clearFilters) {
      clearFilters()
    },
    handleTableChange (pagination, filters, sorter) {
      if (filters.flowId) {
        this.flowQuery.flowId = filters.flowId[0]
      }
      if (filters.flowName) {
        console.log('wdadadwadadwad')
        this.flowQuery.flowName = filters.flowName[0]
      }
      if (filters.disableMark) {
        this.flowQuery.disableMark = filters.disableMark[0]
      }
      this.flowQuery.pageNo = pagination.current
      this.flowQuery.pageSize = pagination.pageSize
      this.flowQuery.sortField = sorter.field
      this.flowQuery.sortOrder = sorter.order
      console.log('pagination', pagination)
      console.log('filters', filters)
      console.log('sorter', sorter)
      console.log('flowQuery', this.flowQuery)
      this.flows(this.flowQuery)
    }
  }
}
</script>

<style lang="less" scoped>
  #flow_head_col {
    font-weight: bold;
    font-size: 16px;
    margin-left: 0px;
    padding: auto;
  }
.disable-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(236, 70, 70));
  border-radius: 3%;
  color: #ffffff;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}
.enable-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(57, 199, 57));
  border-radius: 3%;
  color: #ffffff;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}
.refs-num-tag-app {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  border-radius: 3%;
  padding: 1px 4px;
  background: linear-gradient(#01274b);
//   #1890ff
  color: #ffffff;
  margin:0px;
  font-weight: 500;
  text-align: center;
}
.refs-num-tag-plugin {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  border-radius: 3%;
  padding: 1px 4px;
  background: linear-gradient(rgb(253, 135, 1));
  color: #ffffff;
  margin:0px;
  font-weight: 500;
  text-align: center;
}

@keyframes diable-tag-color {
  0% {
    border-color: #cdcfcf;
    box-shadow: 0 0 1px #cdcfcf, inset 0 0 1px #cdcfcf;
  }
  100% {
    border-color: #cdcfcf;
    box-shadow: 0 0 5px #cdcfcf, inset 0 0 3px #cdcfcf;
  }
}
</style>
