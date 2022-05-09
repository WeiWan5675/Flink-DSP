<template>

  <div id="application-div">
    <a-row :gutter="20">
      <a-col :span="12">
        <a-card :headStyle="{height:'100px'}" :bodyStyle="{height:'20px'}">
          <div slot="title">
            <a-row>
              <a-col>
                <h3 style="margin-bottom:-1px">{{ $t('application.running') }}</h3>
              </a-col>
            </a-row>
            <a-row>
              <a-col>
                <CountTo class="CountToNum" :startVal="applicationOverview.running" :endVal="applicationOverview.running" style="color:#52c41a;font-size:36px;"></CountTo>
              </a-col>
            </a-row>
          </div>
          <p style="fontSize: 14px;color: rgba(0, 0, 0, 0.85); margin-top:-10px;margin-left:-10px;fontWeight: 500">
            <a-row type="flex" justify="start" :gutter="15" style="margin-left:5px">
              <a-col>
                {{ this.$t('application.deployed') }}
                <CountTo class="CountToNum" :endVal="applicationOverview.deployed"></CountTo>
              </a-col>
              <a-divider type="vertical" />
              <a-col>
                {{ this.$t('application.needRestart') }}
                <CountTo class="CountToNum" :endVal="applicationOverview.needRestart"></CountTo>
              </a-col>
            </a-row>
          </p>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card :headStyle="{height:'100px'}" :bodyStyle="{height:'20px'}">
          <div slot="title">
            <a-row>
              <a-col>
                <h3 style="margin-bottom:-1px">{{ $t('application.total') }}</h3>
                <CountTo class="CountToNum" :endVal="applicationOverview.total" style="color:#52c41a;font-size:36px;"></CountTo>
              </a-col>
            </a-row>
            <a-row>
            </a-row>
          </div>
          <p style="fontSize: 14px;color: rgba(0, 0, 0, 0.85); margin-top:-10px;margin-left:-10px;fontWeight: 500">
            <a-row type="flex" justify="start" :gutter="15" style="margin-left:5px">
              <a-col>
                {{ $t('application.finished') }}
                <CountTo :endVal="applicationOverview.finished"></CountTo>
              </a-col>
              <a-divider type="vertical" />
              <a-col>
                {{ $t('application.canceled') }}
                <CountTo class="CountToNum" :endVal="applicationOverview.canceled"></CountTo>
              </a-col>
              <a-divider type="vertical" />
              <a-col>
                {{ $t('application.failed') }}
                <CountTo class="CountToNum" :endVal="applicationOverview.failed"></CountTo>
              </a-col>
            </a-row>
          </p>
        </a-card>
      </a-col>
    </a-row>
    <a-card :bordered="true" :title="$t('application.total')" style="margin-top: 30px">
      <div slot="extra">
        <a-col>
          <router-link to="/dsp/application/create">
            <a-button style="float:right" type="primary" icon="plus" v-action:application:create>
              {{ $t('application.newApp') }}
            </a-button>
          </router-link>
        </a-col>
      </div>
      <a-row style="margin-top: 0px">
        <a-col>
          <a-table
            rowKey="id"
            :columns="columns"
            :pagination="pagination"
            :data-source="data"
            @change="handleTableChange"
            style="background-color: #fff;">
            <slot slot="appState" slot-scope="state">
              <a-tag class="runing-tag" v-if="state.code===1" >{{ $t('dict.appState.running') }}</a-tag>
              <a-tag class="stop-tag" v-if="state.code===2" >{{ $t('dict.appState.stopped') }}</a-tag>
              <a-tag class="cancel-tag" v-if="state.code===3" >{{ $t('dict.appState.canceled') }}</a-tag>
              <a-tag class="finished-tag" v-if="state.code===4" >{{ $t('dict.appState.finished') }}</a-tag>
              <a-tag class="starting-tag" v-if="state.code===5" >{{ $t('dict.appState.starting') }}</a-tag>
              <a-tag class="stoping-tag" v-if="state.code===6" >{{ $t('dict.appState.stopping') }}</a-tag>
              <a-tag class="canceling-tag" v-if="state.code===7" >{{ $t('dict.appState.canceling') }}</a-tag>
              <a-tag class="restarting-tag" v-if="state.code===8" >{{ $t('dict.appState.restarting') }}</a-tag>
              <a-tag class="failed-tag" v-if="state.code===9" >{{ $t('dict.appState.failed') }}</a-tag>
              <a-tag class="error-tag" v-if="state.code===10" >{{ $t('dict.appState.error') }}</a-tag>
              <a-tag class="unknown-tag" v-if="state.code===11" >{{ $t('dict.appState.unknown') }}</a-tag>
              <a-tag class="init-tag" v-if="state.code===12" >{{ $t('dict.appState.init') }}</a-tag>
              <a-tag class="disable-tag" v-if="state.code===13" >{{ $t('dict.appState.disable') }}</a-tag>
              <a-tag class="waiting-tag" v-if="state.code===14" >{{ $t('dict.appState.waiting') }}</a-tag>
            </slot>
            <slot slot="appType" slot-scope="type">
              <a-tag v-if="type.code===1" class="stream-app-tag" >{{ $t('dict.appType.stream') }}</a-tag>
              <a-tag v-if="type.code===2" class="batch-app-tag" >{{ $t('dict.appType.batch') }}</a-tag>
              <a-tag v-if="type.code===3" class="sql-app-tag" >{{ $t('dict.appType.sql') }}</a-tag>
            </slot>
            <span slot="action" slot-scope="text,record">
              <a-button-group>
                <a-tooltip placement="bottom" :title="$t('application.startApp')">
                  <a-button v-action:flow:update @click="startApplication(record)" shape="circle" type="link" >
                    <b-icon-play-circle-fill style="width: 18px; height: 18px;">
                    </b-icon-play-circle-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('application.stopApp')">
                  <a-button v-action:flow:update @click="stopApplication(record)" shape="circle" type="link" >
                    <b-icon-pause-circle-fill style="width: 18px; height: 18px;"></b-icon-pause-circle-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('application.viewLog')">
                  <a-button v-action:flow:update @click="tailApplicationLog(record)" shape="circle" type="link">
                    <b-icon-file-earmark-code-fill style="width: 18px; height: 18px;"></b-icon-file-earmark-code-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('application.updateApp')">
                  <a-button v-action:application:update @click="editApplication(record)" shape="circle" type="link" >
                    <b-icon-tools style="width: 18px; height: 18px;"></b-icon-tools>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('application.deleteApp')">
                  <a-popconfirm :title="$t('application.deleteConfirm')" :ok-text="$t('form.deleteOk')" :cancel-text="$t('form.deleteCancel')" @confirm="deleteApplication(record)">
                    <a-button v-action:flow:update shape="circle" type="link" >
                      <b-icon-trash-fill style="width: 18px; height: 18px;"></b-icon-trash-fill>
                    </a-button>
                  </a-popconfirm>
                </a-tooltip>
              </a-button-group>
              <a-badge
                v-if="record.deployVo.restartMark === 1 && [1,5,6,7,8,14].includes(record.applicationState.code)"
                count="!"
                :title="$t('application.needRestart')"
                :number-style="{
                  backgroundColor: 'white',
                  color: '#faad14',
                  boxShadow: '0 0 0 1px #faad14 inset',
                }"
                :offset="[5,-14]"
              />
            </span>
            <div
              slot="filterDropdown"
              slot-scope="{ setSelectedKeys, selectedKeys, confirm, clearFilters, column }"
              style="padding: 8px"
            >
              <a-input
                v-ant-ref="c => (searchInput = c)"
                :placeholder="$t('form.search') + ' ' + `${column.title}`"
                :value="selectedKeys[0]"
                style="width: 188px; margin-bottom: 8px; display: block;"
                @change="e => setSelectedKeys(e.target.value ? [e.target.value] : [])"
                @pressEnter="() => handleAppNameSearch(selectedKeys, confirm, column.dataIndex)"
              />
              <a-button
                type="primary"
                icon="search"
                size="small"
                style="width: 90px; margin-right: 8px"
                @click="() => handleAppNameSearch(selectedKeys, confirm, column.dataIndex)"
              >
                {{ $t('form.search') }}
              </a-button>
              <a-button size="small" style="width: 90px" @click="() => handleAppNameReset(clearFilters)">
                {{ $t('form.reset') }}
              </a-button>
            </div>
            <a-icon
              slot="filterIcon"
              slot-scope="filtered"
              type="search"
              :style="{ color: filtered ? '#108ee9' : undefined }"
            />
            <div slot="expandedRowRender" slot-scope="record" style="margin: 0">
              <!-- {{ record }} -->
              <a-table
                :columns="row_columns"
                :data-source="[record]"
                bordered
                :pagination="false"
                size="middle"
                rowKey="id"
              />

            </div>
          </a-table>
          <a-modal
            title="启动应用"
            :visible="startApplicationModal"
            :centered="true"
            width="700px"
            height="500px"
            @ok="handleStartAppOk"
            @cancel="handleStartAppCancel">
            <a-form :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
              <a-form-item label="Job Parallelism">
                <a-input-number
                  v-decorator="['jobParallelism', { rules: [{ required: true, message: 'Please input your note!' }] }]"
                  style="width: 100%"
                />
              </a-form-item>
              <a-form-item label="Enabel Checkpoint" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
                <a-switch />
              </a-form-item>
              <a-form-item label="Checkpoint interval">
                <a-input-number
                  v-decorator="['checkpointInterval', { rules: [{ required: true, message: 'Please input your note!' }] }]"
                  style="width: 100%"
                />
              </a-form-item>
              <a-form-item label="From Savepoint" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
                <a-switch />
              </a-form-item>
              <a-form-item label="Savepoint Path" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
                <a-input v-decorator="['savepointPath', { rules: [{ required: true, message: 'Please input your savepoint path!' }] }]" />
              </a-form-item>
            </a-form>
          </a-modal>
          <a-modal
            title="停止应用"
            :visible="stopApplicationModal"
            :centered="true"
            width="800px"
            height="600px"
            @ok="handleStopAppOk"
            @cancel="handleStopAppCancel">
            <p>启动应用...</p>
          </a-modal>
          <a-modal
            title="应用日志"
            :dialog-style="{ top: '20px' }"
            :visible="tailApplicationLogModal"
            :centered="true"
            width="1500px"
            height="800px"
            @ok="handleTailAppOk"
            @cancel="handleTailAppCancel">
            <div id="log-container" style="height: 800px; overflow-y: scroll; background: #202020; color: #ffffff; padding: 10px;">
              <div></div>
            </div>
          </a-modal>
        </a-col>
      </a-row>
    </a-card>

  </div>
</template>

<script>
import * as appTool from '@/api/application'
import moment from 'moment'
import CountTo from 'vue-count-to'
import $ from 'jquery'
export default {
  name: 'Application',
  components: {
    CountTo,
    moment
  },
  computed: {
    row_columns () {
      const columns = [
        {
          title: this.$t('application.appName'),
          dataIndex: 'appName',
          key: 'appName',
          width: 30,
          align: 'center',
          ellipsis: true
        },
        {
          title: this.$t('application.deployInfo'),
          children: [
            {
              title: this.$t('engine.engineType'),
              dataIndex: 'deployVo.engineType.type',
              key: 'engineType',
              width: 50,
              align: 'center'
            },
            {
              title: this.$t('engine.engineMode'),
              dataIndex: 'deployVo.engineMode.type',
              key: 'engineMode',
              width: 50,
              align: 'center'
            },
            {
              title: this.$t('application.jobFile'),
              dataIndex: 'deployVo.jobFile',
              key: 'jobFile',
              width: 50,
              align: 'center',
              ellipsis: true
            }
          ],
          ellipsis: true
        },
        {
          title: this.$t('application.runningState'),
          children: [
            {
              title: this.$t('application.startTime'),
              dataIndex: 'deployVo.startTime',
              key: 'startTime',
              width: 50,
              align: 'center',
              customRender: (text, row, index) => {
                if (text) {
                  return moment(text).format('YYYY-MM-DD HH:mm:ss')
                } else {
                  return '-'
                }
              }
            },
            {
              title: this.$t('application.endTime'),
              dataIndex: 'deployVo.endTime',
              key: 'endTime',
              width: 50,
              align: 'center',
              customRender: (text, row, index) => {
                if (text) {
                  return moment(text).format('YYYY-MM-DD HH:mm:ss')
                } else {
                  return '-'
                }
              }
            },
            {
              title: this.$t('application.duration'),
              dataIndex: 'duration',
              key: 'duration',
              width: 50,
              align: 'center',
              customRender: (text, row, index) => {
                if (!row) return '-'
                if (row.deployVo.startTime && row.deployVo.endTime) {
                  return '-'
                }
                if (row.deployVo.startTime) {
                  var currentTime = new Date().getTime()
                  var timestamp = moment.utc(currentTime - row.deployVo.startTime)
                  console.log(timestamp)
                  if (timestamp) {
                    return timestamp.format('HH:mm:ss')
                  }
                }
              }
            }
          ],
          ellipsis: true
        },
        {
          title: this.$t('application.otherInfo'),
          children: [
            {
              title: this.$t('application.createUser'),
              dataIndex: 'userName',
              key: 'userName',
              width: 50,
              align: 'center'
            },
            {
              title: this.$t('application.updateTime'),
              dataIndex: 'updateTime',
              key: 'updateTime',
              width: 50,
              align: 'center',
              customRender: (text, row, index) => {
                if (text) {
                  return moment(text).format('YYYY-MM-DD HH:mm:ss')
                } else {
                  return '-'
                }
              }
            },
            {
              title: this.$t('application.remark'),
              dataIndex: 'remarkMsg',
              key: 'remarkMsg',
              width: 50,
              align: 'center',
              ellipsis: true
            }
          ],
          ellipsis: true
        }
      ]
      return columns
    },
    columns () {
      const columns = [
        { title: this.$t('application.appName'),
          dataIndex: 'appName',
          key: 'appName',
          width: '10%',
          align: 'center',
          scopedSlots: {
            filterDropdown: 'filterDropdown',
            filterIcon: 'filterIcon'
          }
        },
        { title: this.$t('application.jobId'),
          dataIndex: 'jobId',
          key: 'jobId',
          width: '15%',
          align: 'center',
          scopedSlots: {
            filterDropdown: 'filterDropdown',
            filterIcon: 'filterIcon'
          }
        },
        { title: this.$t('application.appType'),
          dataIndex: 'appType',
          key: 'appType',
          width: '10%',
          align: 'center',
          filters: [
            { text: this.$t('dict.appType.stream'), value: 1 },
            { text: this.$t('dict.appType.batch'), value: 2 },
            { text: this.$t('dict.appType.sql'), value: 3 }
          ],
          scopedSlots: {
            customRender: 'appType'
          }
        },
        { title: this.$t('application.appState'),
          dataIndex: 'applicationState',
          key: 'applicationState',
          filters: [
            { value: 1, text: this.$t('dict.appState.running') },
            { value: 2, text: this.$t('dict.appState.stopped') },
            { value: 3, text: this.$t('dict.appState.canceled') },
            { value: 4, text: this.$t('dict.appState.finished') },
            { value: 5, text: this.$t('dict.appState.starting') },
            { value: 6, text: this.$t('dict.appState.stopping') },
            { value: 7, text: this.$t('dict.appState.canceling') },
            { value: 8, text: this.$t('dict.appState.restarting') },
            { value: 9, text: this.$t('dict.appState.failed') },
            { value: 10, text: this.$t('dict.appState.error') },
            { value: 11, text: this.$t('dict.appState.unknown') },
            { value: 12, text: this.$t('dict.appState.init') },
            { value: 13, text: this.$t('dict.appState.disable') },
            { value: 14, text: this.$t('dict.appState.waiting') }
          ],
          width: '10%',
          align: 'center',
          scopedSlots: {
            customRender: 'appState'
          }
        },
        { title: this.$t('application.flowName'),
          key: 'flowName',
          dataIndex: 'flowName',
          width: '10%',
          align: 'center',
          scopedSlots: {
            filterDropdown: 'filterDropdown',
            filterIcon: 'filterIcon'
          }
        },
        { title: this.$t('application.createTime'),
          key: 'createTime',
          dataIndex: 'createTime',
          width: '15%',
          align: 'center',
          sorter: true,
          customRender: (text, row, index) => {
            return moment(text).format('YYYY-MM-DD HH:mm:ss')
          }
        },
        { title: this.$t('application.remark'),
          key: 'remarkMsg',
          dataIndex: 'remarkMsg',
          width: '15%',
          align: 'center'
        },
        { title: this.$t('form.action'),
          key: 'action',
          width: '15%',
          align: 'center',
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
      appTimer: undefined,
      startApplicationModal: false,
      appModalRecord: {},
      stopApplicationModal: false,
      tailApplicationLogModal: false,
      tailApplicationText: '',
      tailApplicationWebsocket: undefined,
      pagination: {
        total: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100']
      },
      applicationQuery: {
        jobId: undefined,
        appType: undefined,
        appState: undefined,
        appName: undefined,
        flowName: undefined,
        sortField: 'createTime',
        sortOrder: 'descend',
        pageNo: 1,
        pageSize: 10
      },
      applicationOverview: {
        running: 0,
        deployed: 0,
        needRestart: 0,
        total: 0,
        canceled: 0,
        finished: 0,
        failed: 0
      },
      applicationStates: [],
      applicationTypes: [],
      appStates: [],
      appTypes: [],
      startVal: 0,
      endVal: 3000,
      data: []
    }
  },
  created () {
    this.applications(this.applicationQuery)
    this.getApplicationOverview()
    this.getApplicationStates()
    this.getApplicationType()
  },
  mounted () {
    this.appTimer = setInterval(f => {
      this.applications(this.applicationQuery)
      this.getApplicationOverview()
    }, 10000)
  },
  beforeDestroy () {
    clearInterval(this.appTimer)
  },
  methods: {
    applications (params) {
      appTool.list(params).then(r => {
        if (r.success) {
          this.data = r.result.data
          this.pagination.total = r.result.totalCount
          this.pagination.current = r.result.pageNo
          this.pagination.pageSize = r.result.pageSize
        } else {
          console.log('error')
        }
      })
    },
    getApplicationOverview () {
      appTool.getApplicationOverview().then(r => {
        if (r.success) {
          this.applicationOverview = r.result
        }
      })
    },
    getApplicationStates () {
      appTool.getApplicationStates().then(r => {
        if (r.success) {
          this.applicationStates = r.result
          this.applicationStates.forEach(s => this.appStates.push({ text: s.state, value: s.code }))
        }
      })
    },
    getApplicationType () {
      appTool.applicationType().then(r => {
        if (r.success) {
          this.applicationTypes = r.result
          this.applicationTypes.forEach(s => this.appTypes.push({ text: s.type, value: s.code }))
        }
      })
    },
    handleAppNameSearch (selectedKeys, confirm, dataIndex) {
      confirm()
    },
    handleAppNameReset (clearFilters) {
      clearFilters()
    },
    handleTableChange (pagination, filters, sorter) {
      if (filters.appName) {
        this.applicationQuery.appName = filters.appName[0]
      }
      if (filters.jobId) {
        this.applicationQuery.jobId = filters.jobId[0]
      }
      if (filters.flowName) {
        this.applicationQuery.flowName = filters.flowName[0]
      }
      if (filters.applicationState) {
        console.log('applicationState f: ', filters.applicationState)
        this.applicationQuery.applicationState = filters.applicationState
      }
      if (filters.appType) {
        console.log('appType f: ', filters.appType)
        this.applicationQuery.appType = filters.appType
      }
      this.applicationQuery.pageNo = pagination.current
      this.applicationQuery.pageSize = pagination.pageSize
      this.applicationQuery.sortField = sorter.field
      this.applicationQuery.sortOrder = sorter.order
      console.log('pagination', pagination)
      console.log('filters', filters)
      console.log('sorter', sorter)
      console.log('applicationQuery', this.applicationQuery)

      this.applications(this.applicationQuery)
    },
    editApplication (record) {
      console.log(record)
      this.$router.push({ path: '/dsp/application/create', query: { record: record } })
    },
    deleteApplication (record) {
      console.log('record', record)
      const params = {
        appId: record.id
      }
      appTool.deleteApplication(params).then(r => {
        console.log('response: ', r)
        if (r.success) {
          this.$notification.success({
            message: this.$t('message.success'),
            description: this.$t('message.application.delete_success')
          })
          setTimeout(() => {
            this.applications(this.applicationQuery)
            this.applicationOverview()
          }, 1000)
        } else {
          this.$notification.error({
            message: this.$t('message.failed'),
            description: r.message || this.$t('message.application.delete_failed')
          })
        }
      })
    },
    startApplication (record) {
      console.log('startApplication record', record)
      this.appModalRecord = record
      this.startApplicationModal = true
    },
    stopApplication (record) {
      console.log('stopApplication record', record)
      this.appModalRecord = record
      this.stopApplicationModal = true
    },
    tailApplicationLog (record) {
      //   tailApplicationText
      this.appModalRecord = record
      $('#log-container div').empty()
      // websocket对象
      const url = 'ws://' + location.host + '/websocket/application/logs/' + record.jobId
      this.tailApplicationWebsocket = new WebSocket(url)
      // 连接发生错误的回调方法
      this.tailApplicationWebsocket.onerror = function (e) {
        console.error('WebSocket连接发生错误', e)
      }
      this.tailApplicationWebsocket.onopen = function () {
        console.log('WebSocket连接成功')
      }
      // 接收到消息的回调方法
      this.tailApplicationWebsocket.onmessage = function (event) {
        // 追加
        if (event.data) {
          // 接收服务端的实时日志并添加到HTML页面中
          $('#log-container div').append(event.data)
          // 滚动条滚动到最低部
          $('#log-container').scrollTop($('#log-container div').height() - $('#log-container').height())
        }
      }
      // 连接关闭的回调方法
      this.tailApplicationWebsocket.onclose = function () {
        console.log('WebSocket连接关闭')
      }

      this.tailApplicationLogModal = true
    },

    handleStartAppOk () {
      console.log('handleStartAppOk')
      appTool.startApplication(this.appModalRecord).then(r => {
        console.log('response: ', r)
        if (r.success) {
          this.$notification.success({
            message: this.$t('message.success'),
            description: this.$t('message.application.delete_success')
          })
          setTimeout(() => {
            this.applications(this.applicationQuery)
            this.getApplicationOverview()
          }, 1000)
        } else {
          this.$notification.error({
            message: this.$t('message.failed'),
            description: r.message || this.$t('message.application.delete_failed')
          })
        }
      })
      this.handleAppModalOk()
    },
    handleStartAppCancel () {
      console.log('handleStartAppCancel')
      this.handleAppModalCancel()
    },
    handleStopAppOk () {
      console.log('handleStopAppOk')
      appTool.stopApplication(this.appModalRecord).then(r => {
        console.log('response: ', r)
        if (r.success) {
          this.$notification.success({
            message: this.$t('message.success'),
            description: this.$t('message.application.delete_success')
          })
          setTimeout(() => {
            this.applications(this.applicationQuery)
            this.getApplicationOverview()
          }, 1000)
        } else {
          this.$notification.error({
            message: this.$t('message.failed'),
            description: r.message || this.$t('message.application.delete_failed')
          })
        }
      })
      this.handleAppModalOk()
    },
    handleStopAppCancel () {
      console.log('handleStopAppCancel')
      this.handleAppModalCancel()
    },
    handleTailAppOk () {
      console.log('handleTailAppOk')
      this.tailApplicationWebsocket.close()
      this.tailApplicationWebsocket = undefined
      this.handleAppModalOk()
    },
    handleTailAppCancel () {
      console.log('handleTailAppCancel')
      this.tailApplicationWebsocket.close()
      this.tailApplicationWebsocket = undefined
      this.handleAppModalCancel()
    },
    handleAppModalOk (e) {
      this.startApplicationModal = false
      this.tailApplicationLogModal = false
      this.stopApplicationModal = false
      this.appModalRecord = {}
    },
    handleAppModalCancel (e) {
      this.startApplicationModal = false
      this.tailApplicationLogModal = false
      this.stopApplicationModal = false
      this.appModalRecord = {}
    }

  }
}
</script>

<style lang="less" scoped>
  #app_table_head_class{
    font-weight: bold;
    font-size: 16px;
    margin-left: 5px;
    padding: auto;
  }
  .CountToNum {
      margin-left: 5px;
  }
  .app-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  width: 100px;
  height: auto;
  border-radius: 3%;
  color: #77c7c7
  }
.runing-tag {
    background: linear-gradient(#24cf3b);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.stop-tag {
    background: linear-gradient(#005ce6);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.cancel-tag {
    background: linear-gradient(#f99828);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.finished-tag {
    background: linear-gradient(#40a9ff);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.starting-tag {
    background: linear-gradient(#4ce68c);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.stoping-tag {
    background: linear-gradient(#264da8);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.canceling-tag {
    background: linear-gradient(#f1c662);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.restarting-tag {
    background: linear-gradient(#00581a);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.failed-tag {
    background: linear-gradient(#f5222d);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.error-tag {
    background: linear-gradient(#f5222d);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.unknown-tag {
    background: linear-gradient(#6d6d6d);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
        min-width: 80px;
}
.init-tag {
    background: linear-gradient(#44a06f);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.disable-tag {
    background: linear-gradient(#1f2727);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.waiting-tag {
    background: linear-gradient(#5a3f95);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}
.stream-app-tag {
    background: linear-gradient(#0095c2e3);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}

.batch-app-tag {
    background: linear-gradient(#84c002);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
}

.sql-app-tag {
    background: linear-gradient(#810076);
    animation: disable-tag-color 800ms ease-out infinite alternate;
    border-radius: 3%;
    color: #ffffff;
    font-weight: 600;
    text-align: center;
    padding: 3px 5px;
    min-width: 80px;
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
