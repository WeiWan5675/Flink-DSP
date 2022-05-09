<template>
  <a-card :bordered="true" :title="$t('appCreate.appDetails')" style="margin-top: 0px;">
    <!-- {{ applicationVo }}
    -------------------------------------------------
    {{ customVars }} -->
    <a-form>
      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('appCreate.appName')"
        :validate-status="validateInfo.appName.validateStatus + ''"
        :has-feedback="validateInfo.appName.hasFeedback"
        :help="validateInfo.appName.help"
        required
      >
        <a-input v-model="applicationVo.appName" :placeholder="$t('appCreate.inputAppName')" @blur="validateAppName()"/>
      </a-form-item>
      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('appCreate.appType')"
        :validate-status="validateInfo.appType.validateStatus + ''"
        :has-feedback="validateInfo.appType.hasFeedback"
        :help="validateInfo.appType.help"
        required
      >
        <a-select v-model="appTypeCode" @change="handleAppTypeChange" @blur="validateAppType()" :placeholder="$t('appCreate.chooseAppType')">
          <a-select-option :disabled="[2,3].includes(item.code)" :key="item.code" v-for="item in appTypes">
            {{ item.type }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('appCreate.appFlow')"
        :validate-status="validateInfo.flowId.validateStatus + ''"
        :has-feedback="validateInfo.flowId.hasFeedback"
        :help="validateInfo.flowId.help"
        required
      >
        <a-select v-model="applicationVo.flowId" @blur="validateFlowId()" @change="validateFlowId()" mode="multiple" :placeholder="$t('appCreate.chooseAppFlow')">
          <a-select-option :key="flow.id" v-for="flow in flowList">
            {{ flow.flowName }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('appCreate.engineType')"
        :validate-status="validateInfo.engineType.validateStatus + ''"
        :has-feedback="validateInfo.engineType.hasFeedback"
        :help="validateInfo.engineType.help"
        required
      >
        <a-select v-model="engineTypeCode" @change="handleEngineTypeChange" @blur="validateEngineType()" :placeholder="$t('appCreate.chooseEngineType')">
          <a-select-option :disabled="[2,3].includes(item.code)" :key="item.code" v-for="item in engineTypes">
            {{ item.type }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('appCreate.engineMode')"
        :validate-status="validateInfo.engineMode.validateStatus + ''"
        :has-feedback="validateInfo.engineMode.hasFeedback"
        :help="validateInfo.engineMode.help"
        required
      >
        <a-select v-model="engineModeCode" @change="handleEngineModeChange" @blur="validateEngineMode()" :placeholder="$t('appCreate.chooseEngineMode')">
          <a-select-option :disabled="[2,4,5,7,8,9,10].includes(item.code)" :key="item.code" v-for="item in engineModes">
            {{ item.type }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('appCreate.engineConfig')"
      >
        <ConfigOptions :value="applicationVo.configVo.core.engineConfig.engineConfigs" @change="handleEngineConfigsChange"></ConfigOptions>
      </a-form-item>
      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('appCreate.otherConfig')"
      >
        <a-tabs :tabBarStyle="{fontWeight: 650, fontSize: 'inherit'}" @change="otherConfigTabsChange">
          <a-tab-pane key="1">
            <span slot="tab">
              <a-icon type="swap" />
              {{ $t('appCreate.speedLimit') }}
            </span>
            <a-card :bordered="true" style="padding: 16px" :bodyStyle="{padding: '0px'}">
              <a-form layout="horizontal" labelAlign="left">
                <a-form-item
                  :label-col="{span: 4}"
                  :wrapper-col="{span: 19}"
                  :label="$t('appCreate.readSpeed')"
                  style="margin-bottom: 8px"
                >
                  <a-input-number id="readSpeed" style="width: 100%" v-model="applicationVo.configVo.core.speedLimiter.readSpeed" :disabled="!otherConfigTabs.showSpeedLimitTable"></a-input-number>
                </a-form-item>
                <a-form-item
                  :label-col="{span: 4}"
                  :wrapper-col="{span: 19}"
                  :label="$t('appCreate.processSpeed')"
                  style="margin-bottom: 8px"
                >
                  <a-input-number id="processSpeed" style="width: 100%" v-model="applicationVo.configVo.core.speedLimiter.processSpeed" :disabled="!otherConfigTabs.showSpeedLimitTable"></a-input-number>
                </a-form-item>
                <a-form-item
                  :label-col="{span: 4}"
                  :wrapper-col="{span: 19}"
                  :label="$t('appCreate.writeSpeed')"
                  style="margin-bottom: 8px"
                >
                  <a-input-number id="writeSpeed" style="width: 100%" v-model="applicationVo.configVo.core.speedLimiter.writeSpeed" :disabled="!otherConfigTabs.showSpeedLimitTable"></a-input-number>
                </a-form-item>
                <a-form-item
                  :label-col="{span: 4}"
                  :wrapper-col="{span: 19}"
                  :label="$t('appCreate.timeUnit')"
                  style="margin-bottom: 8px"
                >
                  <a-input-number id="samplingInterval" style="width: 100%" v-model="applicationVo.configVo.core.speedLimiter.samplingInterval" :disabled="!otherConfigTabs.showSpeedLimitTable"></a-input-number>
                </a-form-item>
              </a-form>
            </a-card>
          </a-tab-pane>
          <a-tab-pane key="2">
            <span slot="tab">
              <a-icon type="file-sync" />
              {{ $t('appCreate.unresolved') }}
            </span>
            <a-card :bordered="true" style="padding: 16px" :bodyStyle="{padding: '0px'}">
              <!-- unresolvedHandlers -->
              <a-form layout="horizontal" labelAlign="left">
                <a-form-item
                  :label-col="{span: 4}"
                  :wrapper-col="{span: 19}"
                  :label="$t('appCreate.unresolvedHandler')"
                  style="margin-bottom: 8px"
                >
                  <a-select style="width: 100%" v-model="unresolvedHandlerCode" :placeholder="$t('appCreate.unresolvedHandlerDesc')" @change="handleCollectorHandlerChange" :disabled="!otherConfigTabs.showUnresolvedTable">
                    <a-select-option v-for="item in unresolvedHandlers" :key="item.code" :disabled="[2,3].includes(item.code)" >
                      {{ item.type }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item
                  :label-col="{span: 4}"
                  :wrapper-col="{span: 19}"
                  :label="$t('appCreate.timeUnit')"
                  style="margin-bottom: 8px"
                >
                  <a-input-number id="samplingInterval" style="width: 100%" v-model="applicationVo.configVo.core.unresolvedCollector.samplingInterval" :disabled="!otherConfigTabs.showUnresolvedTable"></a-input-number>
                </a-form-item>
                <a-form-item
                  :label-col="{span: 4}"
                  :wrapper-col="{span: 19}"
                  :label="$t('appCreate.maxAmount')"
                  style="margin-bottom: 8px"
                >
                  <a-input-number id="maxSamplingRecord" style="width: 100%" v-model="applicationVo.configVo.core.unresolvedCollector.maxSamplingRecord" :disabled="!otherConfigTabs.showUnresolvedTable"></a-input-number>
                </a-form-item>
                <a-form-item
                  :label-col="{span: 4}"
                  :wrapper-col="{span: 19}"
                  :label="$t('appCreate.handlerConfig')"
                  style="margin-bottom: 8px"
                >
                  <ConfigOptions :value="applicationVo.configVo.core.unresolvedCollector.handlerConfigs" @change="handleUnresolvedConfigsChange"></ConfigOptions>
                </a-form-item>
              </a-form>
            </a-card>
          </a-tab-pane>
          <a-tab-pane key="3" :disabled="'2,5,6,8'.includes(engineModeCode + '')">
            <span slot="tab">
              <a-icon type="fund" />
              {{ $t('appCreate.metrics') }}
            </span>
            <a-card :bordered="true" style="padding: 16px" :bodyStyle="{padding: '0px'}">
              <a-alert
                :message="$t('appCreate.metricsMsg')"
                :description="$t('appCreate.metricsDesc')"
                type="warning"
                show-icon
              />
              <a-form layout="horizontal" labelAlign="left">
                <a-form-item
                  :label-col="{span: 3}"
                  :wrapper-col="{span: 20}"
                  :label="$t('appCreate.reporter')"
                  style="margin-bottom: 8px"
                >
                  <a-input id="reporter" style="width: 100%" v-model="applicationVo.configVo.core.metricConfig.reporter" />
                </a-form-item>
                <a-form-item
                  :label-col="{span: 3}"
                  :wrapper-col="{span: 20}"
                  :label="$t('appCreate.reportInterval')"
                  style="margin-bottom: 8px"
                >
                  <a-input-number id="interval" style="width: 100%" v-model="applicationVo.configVo.core.metricConfig.interval" />
                </a-form-item>
                <a-form-item
                  :label-col="{span: 3}"
                  :wrapper-col="{span: 20}"
                  :label="$t('appCreate.reportHost')"
                  style="margin-bottom: 8px"
                >
                  <a-input id="host" style="width: 100%" v-model="applicationVo.configVo.core.metricConfig.host" />
                </a-form-item>
                <a-form-item
                  :label-col="{span: 3}"
                  :wrapper-col="{span: 20}"
                  :label="$t('appCreate.reportPort')"
                  style="margin-bottom: 8px"
                >
                  <a-input-number id="port" style="width: 100%" v-model="applicationVo.configVo.core.metricConfig.port" />
                </a-form-item>
              </a-form>
            </a-card>
          </a-tab-pane>
          <span slot="tabBarExtraContent">
            <a-switch v-model="otherConfigTabs.showSpeedLimitTable" v-if="otherConfigTabs.currentTab === '1'" @change="onEnableSpeedChange" />
            <a-switch v-model="otherConfigTabs.showUnresolvedTable" v-if="otherConfigTabs.currentTab === '2'" @change="onEnableUnresolvedChange" />
          </span>
        </a-tabs>
      </a-form-item>
      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('appCreate.customConfig')"
      >
        <EditTable :value="customVars" :title="$t('appCreate.customVars')" @change="handleCustomVarsChange"></EditTable>
      </a-form-item>

      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('App Schedule')"
      >
        <a-checkbox :checked="applicationVo.timerVo.enableStartTimer" @change="appScheduleChange(1)">
          定时启动
        </a-checkbox>
        <a-checkbox :checked="applicationVo.timerVo.enableStopTimer" @change="appScheduleChange(2)">
          定时停止
        </a-checkbox>
        <a-checkbox :checked="applicationVo.timerVo.enableRestartTimer" @change="appScheduleChange(3)">
          定时重启
        </a-checkbox>
        <!-- <a-card :bordered="true" style="padding: 5px" :bodyStyle="{padding: '0px'}"> -->
        <!-- unresolvedHandlers -->
        <a-form layout="horizontal" labelAlign="left">
          <a-form-item
            v-if="applicationVo.timerVo.enableStartTimer"
            :wrapper-col="{span: 24}"
          >
            <CronExpressions
              :value="applicationVo.timerVo.startTimerCron"
              title="Start Timer"
              @change="handleAppTimerCronChange(1, $event)"
              :check="handleStartTimerCheck"
            />
          </a-form-item>
          <a-form-item
            v-if="applicationVo.timerVo.enableStopTimer"
            :wrapper-col="{span: 24}"
          >
            <CronExpressions
              :value="applicationVo.timerVo.stopTimerCron"
              title="Stop Timer"
              @change="handleAppTimerCronChange(2, $event)"
              :check="handleStopTimerCheck"
            />
          </a-form-item>
          <a-form-item
            v-if="applicationVo.timerVo.enableRestartTimer"
            :wrapper-col="{span: 24}"
          >
            <CronExpressions
              :value="applicationVo.timerVo.restartTimerCron"
              title="Restart Timer"
              @change="handleAppTimerCronChange(3, $event)"
              :check="handleRestartTimerCheck"
            />
          </a-form-item>
        </a-form>
        <!-- </a-card> -->
      </a-form-item>
      <a-form-item
        :label-col="{span: 7}"
        :wrapper-col="{span: 12}"
        :label="$t('appCreate.remarkMsg')"
      >
        <a-textarea
          v-model="applicationVo.remarkMsg"
          :placeholder="$t('appCreate.remarkMsgDesc')"
          :auto-size="{ minRows: 6, maxRows: 8 }"
        />
      </a-form-item>
      <a-form-item
        :wrapper-col="{ span: 12, offset: 7 }"
      >
        <a-button style="width: 90px; margin-right: 35px" type="primary" @click="saveApplication()" :loading="appSaveBtnLoading">
          Save
        </a-button>
        <a-button style="width: 90px;" @click="cancelApplication()">
          Cancel
        </a-button>
      </a-form-item>
    </a-form>
  </a-card>
</template>
<script>

import * as appTool from '@/api/application'
import * as flowTool from '@/api/flow'
import ConfigOptions from '@/components/ConfigOptions'
import EditTable from '@/components/EditTable'
import CronExpressions from '@/components/CronExpressions'
function sleep (ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}
export default {
  name: 'ApplicationCreate',
  components: {
    ConfigOptions,
    EditTable,
    CronExpressions
  },
  data () {
    return {
      applicationVo: {
        id: undefined,
        jobId: undefined,
        appName: undefined,
        appType: {
          code: undefined,
          type: undefined
        },
        applicationState: {},
        deployVo: {},
        configVo: {
          core: {
            engineConfig: {
              engineConfigs: {}
            },
            speedLimiter: {
              enableLimiter: false,
              readSpeed: 999999,
              processSpeed: 999999,
              writeSpeed: 999999,
              samplingInterval: 30
            },
            unresolvedCollector: {
              enableCollector: false,
              collectorHandler: {},
              handlerConfigs: {},
              maxSamplingRecord: 999999,
              samplingInterval: 999999
            },
            metricConfig: {
              reporter: 'com.weiwan.dsp.metrics.reporter.HttpPushReport',
              interval: 30,
              host: '127.0.0.1',
              port: 9875

            }
          },
          custom: {
          }
        },
        timerVo: {
          enableStartTimer: false,
          startTimerCron: '* * * * * ?',
          enableStopTimer: false,
          stopTimerCron: '* * * * * ?',
          enableRestartTimer: false,
          restartTimerCron: '* * * * * ?'
        },
        engineType: {
        },
        engineMode: {},
        flowId: [],
        flowName: undefined,
        deployId: undefined,
        createTime: undefined,
        updateTime: undefined,
        userId: undefined,
        userName: undefined,
        remarkMsg: undefined
      },
      isUpdate: false,
      engineModeCode: undefined,
      engineTypeCode: undefined,
      appTypeCode: undefined,
      unresolvedHandlerCode: undefined,
      appTypes: [
        { type: this.$t('dict.appType.stream'), code: 1 },
        { type: this.$t('dict.appType.batch'), code: 2 },
        { type: this.$t('dict.appType.sql'), code: 3 }
      ],
      engineTypes: [
        { engineClass: 'com.weiwan.dsp.core.engine.flink.FlinkJobFlowEngine', code: 1, type: 'FLINK' },
        { engineClass: 'com.weiwan.dsp.core.engine.spark.SparkJobFlowEngine', code: 2, type: 'SPARK' },
        { engineClass: 'com.weiwan.dsp.core.engine.local.LocalJobFlowEngine', code: 3, type: 'LOCAL' }
      ],
      unresolvedHandlers: [
        { type: 'Http', handlerClass: 'com.weiwan.dsp.core.resolve.http.HttpUnresolvedDataCollector', code: 1 },
        { type: 'Jdbc', handlerClass: 'com.weiwan.dsp.core.resolve.jdbc.JDBCUnresolvedDataCollector', code: 2 },
        { type: 'Kafka', handlerClass: 'com.weiwan.dsp.core.resolve.kafka.KafkaUnresolvedDataCollector', code: 3 },
        { type: 'Log', handlerClass: 'com.weiwan.dsp.core.resolve.logging.LogUnresolvedDataCollector', code: 4 }
      ],
      engineModes: [
      ],
      flowList: [],

      validateInfo: {
        appName: {
          validateStatus: '',
          help: '',
          hasFeedback: false,
          updateAppName: undefined
        },
        appType: {
          validateStatus: '',
          help: '',
          hasFeedback: false
        },
        flowId: {
          validateStatus: '',
          help: '',
          hasFeedback: false
        },
        engineType: {
          validateStatus: '',
          help: '',
          hasFeedback: false
        },
        engineMode: {
          validateStatus: '',
          help: '',
          hasFeedback: false
        }
      },
      appTimerValidateInfo: {
        stopCron: {
          status: true,
          message: ''
        },
        startCron: {
          status: true,
          message: ''
        },
        restartCron: {
          status: true,
          message: ''
        }
      },
      otherConfigTabs: {
        currentTab: '1',
        showSpeedLimitTable: true,
        showUnresolvedTable: true
      },
      customVars: [
      ],
      appSaveBtnLoading: false
    }
  },
  created () {
    if (this.$route.query) {
      const record = this.$route.query.record
      if (!record) {
        this.unresolvedHandlerCode = 4
        this.onEnableSpeedChange(false)
        this.onEnableUnresolvedChange(false)
        this.handleCollectorHandlerChange(4)
      } else {
        this.isUpdate = true
        console.log('application: ', record)
        this.applicationVo = record
        this.appTypeCode = this.applicationVo.appType.code
        this.engineTypeCode = this.applicationVo.engineType.code
        this.handleEngineTypeChange(this.engineTypeCode)
        this.engineModeCode = this.applicationVo.engineMode.code
        // 保存更新时传过来的原始appName
        this.validateInfo.appName.updateAppName = this.applicationVo.appName
        this.validateAppForm()
        this.onEnableSpeedChange(this.applicationVo.configVo.core.speedLimiter.enableLimiter)
        this.onEnableUnresolvedChange(this.applicationVo.configVo.core.unresolvedCollector.enableCollector)
        this.unresolvedHandlerCode = this.applicationVo.configVo.core.unresolvedCollector.collectorHandler.code
        this.handleCollectorHandlerChange(this.unresolvedHandlerCode)
        this.customVars = Object.values(this.applicationVo.configVo.custom)
        if (this.applicationVo.timerVo === null || this.applicationVo.timerVo === undefined) {
          this.applicationVo.timerVo = {
            enableStartTimer: false,
            startTimerCron: '* * * * * ?',
            enableStopTimer: false,
            stopTimerCron: '* * * * * ?',
            enableRestartTimer: false,
            restartTimerCron: '* * * * * ?'
          }
        }
      }
    }
    this.getFlows()
  },
  mounted () {
  },
  methods: {
    async saveApplication () {
      this.appSaveBtnLoading = true
      this.validateAppForm()
      try {
        this.validateAppTimer()
      } catch (e) {
        this.appSaveBtnLoading = false
        this.$message.error(this.$t('appCreate.notifyValidateError') + ' message: ' + e.message)
        return
      }
      let validateFlag = true
      await sleep(500)
      Object.values(this.validateInfo).forEach(item => {
        if (item.validateStatus !== 'success') {
          validateFlag = false
        }
      })
      if (!validateFlag) {
        this.appSaveBtnLoading = false
        this.$message.error(this.$t('appCreate.notifyValidateError'))
        return
      }
      this.appSaveBtnLoading = false
      if (this.isUpdate) {
        appTool.updateApplication(this.applicationVo).then(r => {
          if (r.success) {
            console.log('保存成功', r)
            this.$router.push({ path: '/dsp/application' })
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
        appTool.saveApplication(this.applicationVo).then(r => {
          if (r.success) {
            console.log('保存成功', r)
            this.$router.push({ path: '/dsp/application' })
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
      console.log('保存!!!!!!!!!!!!!!', this.applicationVo)
    },
    cancelApplication () {
      this.applicationVo = undefined
      this.appTypeCode = undefined
      this.engineModeCode = undefined
      this.engineTypeCode = undefined
      this.unresolvedHandlerCode = undefined
      this.validateInfo = undefined
      this.$router.push({ path: '/dsp/application' })
    },
    validateAppForm () {
      this.validateAppName()
      this.validateAppType()
      this.validateFlowId()
      this.validateEngineType()
      this.validateEngineMode()
    },
    validateAppName () {
      console.log('validateAppName')
      this.validateInfo.appName.validateStatus = 'validating'
      this.validateInfo.appName.hasFeedback = true
      const appName = this.applicationVo.appName
      const applicationQuery = {
        appName: undefined
      }
      if (this.validateInfo.appName.updateAppName !== undefined && appName === this.validateInfo.appName.updateAppName) {
        this.validateInfo.appName.hasFeedback = true
        this.validateInfo.appName.validateStatus = 'success'
        return
      }
      if (appName && appName !== '') {
        applicationQuery.appName = appName
        console.log('params', applicationQuery)
        appTool.queryApp(applicationQuery).then(r => {
          if (r.success) {
            if (r.result) {
              this.validateInfo.appName.hasFeedback = true
              this.validateInfo.appName.validateStatus = 'error'
              this.validateInfo.appName.help = this.$t('appCreate.duplicateAppName')
            } else {
              this.validateInfo.appName.hasFeedback = true
              this.validateInfo.appName.validateStatus = 'success'
              this.validateInfo.appName.help = ''
            }
          } else {
            this.validateInfo.appName.validateStatus = 'error'
            this.validateInfo.appName.hasFeedback = true
          }
        })
      } else {
        this.validateInfo.appName.validateStatus = 'error'
        this.validateInfo.appName.hasFeedback = true
        this.validateInfo.appName.help = this.$t('appCreate.inputAppName')
      }
    },
    validateAppType () {
      console.log('validateAppType')
      if (this.applicationVo.appType.code !== undefined) {
        this.validateInfo.appType.hasFeedback = true
        this.validateInfo.appType.validateStatus = 'success'
        this.validateInfo.appType.help = ''
      } else {
        this.validateInfo.appType.hasFeedback = true
        this.validateInfo.appType.validateStatus = 'error'
        this.validateInfo.appType.help = this.$t('appCreate.chooseAppType')
      }
    },
    validateAppTimer () {
      console.log('validateAppTimer')
      Object.values(this.appTimerValidateInfo).forEach(item => {
        if (!item.status) {
          throw new Error(item.message)
        }
      })
    },
    validateFlowId () {
      if (this.applicationVo.flowId.length > 1) {
        this.validateInfo.flowId.validateStatus = 'error'
        this.validateInfo.flowId.hasFeedback = true
        this.validateInfo.flowId.help = this.$t('appCreate.oneFlowOnly')
      } else if (this.applicationVo.flowId.length === 0) {
        this.validateInfo.flowId.validateStatus = 'error'
        this.validateInfo.flowId.hasFeedback = true
        this.validateInfo.flowId.help = this.$t('appCreate.chooseAppFlow')
      } else {
        this.validateInfo.flowId.validateStatus = 'success'
        this.validateInfo.flowId.hasFeedback = true
        this.validateInfo.flowId.help = ''
      }
    },
    validateEngineType () {
      console.log('validateEngineType')
      if (this.applicationVo.engineType.code !== undefined) {
        this.validateInfo.engineType.hasFeedback = true
        this.validateInfo.engineType.validateStatus = 'success'
        this.validateInfo.engineType.help = ''
      } else {
        this.validateInfo.engineType.hasFeedback = true
        this.validateInfo.engineType.validateStatus = 'error'
        this.validateInfo.engineType.help = this.$t('appCreate.chooseEngineType')
      }
    },
    validateEngineMode () {
      console.log('validateEngineMode')
      if (this.applicationVo.engineType.code === undefined) {
        this.validateInfo.engineMode.hasFeedback = true
        this.validateInfo.engineMode.validateStatus = 'error'
        this.validateInfo.engineMode.help = this.$t('appCreate.chooseEngineTypeFirst')
      } else if (this.applicationVo.engineMode.code !== undefined) {
        this.validateInfo.engineMode.hasFeedback = true
        this.validateInfo.engineMode.validateStatus = 'success'
        this.validateInfo.engineMode.help = ''
      } else {
        this.validateInfo.engineMode.hasFeedback = true
        this.validateInfo.engineMode.validateStatus = 'error'
        this.validateInfo.engineMode.help = this.$t('appCreate.chooseEngineMode')
      }
    },
    applicationFormChange (props, values) {
      console.log('props, values', props, values)
    },
    handleEngineConfigsChange (data) {
      console.log('data-watch:', data)
      Object.values(data).forEach(o => {
        if (this.applicationVo.configVo.core.engineConfig.engineConfigs[o.key] !== undefined) {
          console.log('存在!!!')
          this.applicationVo.configVo.core.engineConfig.engineConfigs[o.key] = o
        } else {
          console.log('不存在!!!!!!')
        }
      })
      console.log('data-change:', this.applicationVo.configVo.core.engineConfig.engineConfigs)
    },
    resetForm () {
      this.$refs.ruleForm.resetFields()
    },
    handleEngineTypeChange (value) {
      console.log('handleEngineTypeChange', value)
      this.engineTypeCode = value
      this.applicationVo.engineType = this.engineTypes.filter(item => item.code === this.engineTypeCode)[0]
      this.applicationVo.configVo.core.engineConfig.engineType = this.engineTypes.filter(item => item.code === this.engineTypeCode)[0]
      this.getEngineMode(value)
    },
    handleEngineModeChange (value) {
      console.log('handleEngineModeChange', value)
      this.engineModeCode = value
      this.applicationVo.engineMode = this.engineModes.filter(item => item.code === this.engineModeCode)[0]
      this.applicationVo.configVo.core.engineConfig.engineMode = this.engineModes.filter(item => item.code === this.engineModeCode)[0]
      this.getEngineConfig()
    },
    handleAppTypeChange (value) {
      console.log('handleAppTypeChange', value)
      this.appTypeCode = value
      this.applicationVo.appType = this.appTypes.filter(item => item.code === this.appTypeCode)[0]
    },

    handleFlowChange (value) {
      this.flow = value
      console.log('this flow', this.flow)
    },
    handleCollectorHandlerChange (value) {
      if (value) {
        this.unresolvedHandlerCode = value
        this.applicationVo.configVo.core.unresolvedCollector.collectorHandler = this.unresolvedHandlers.filter(item => item.code === value)[0]
        this.getCollectorHandlerConfigs()
      }
    },
    getCollectorHandlerConfigs () {
      const params = {}
      params.collectorClass = this.applicationVo.configVo.core.unresolvedCollector.collectorHandler.handlerClass
      appTool.getCollectorHandlerConfig(params).then(r => {
        if (r.success) {
          this.applicationVo.configVo.core.unresolvedCollector.handlerConfigs = r.result
          console.log('this.applicationVo.configVo.core.unresolvedCollector.handlerConfigs', this.applicationVo.configVo.core.unresolvedCollector.handlerConfigs)
        } else {
          console.log('error')
        }
      })
    },
    handleUnresolvedConfigsChange (data) {
      console.log('data-watch:', data)
      Object.values(data).forEach(o => {
        if (this.applicationVo.configVo.core.unresolvedCollector.handlerConfigs[o.key] !== undefined) {
          console.log('存在!!!')
          this.applicationVo.configVo.core.unresolvedCollector.handlerConfigs[o.key] = o
        } else {
          console.log('不存在!!!!!!')
        }
      })
      console.log('data-change:', this.applicationVo.configVo.core.unresolvedCollector.handlerConfigs)
    },
    getUnresolvedHandlers () {
      appTool.getUnresolvedHandlerType().then(r => {
        if (r.success) {
          this.unresolvedHandlers = r.result
        } else {
          console.log('error')
        }
      })
    },
    getEngineType () {
    //   appTool.engineType().then(r => {
    //     if (r.success) {
    //       this.engineTypes = r.result
    //     } else {
    //       console.log('error')
    //     }
    //   })
    },
    getApplicationType () {
      appTool.applicationType().then(r => {
        if (r.success) {
          this.applicationTypes = r.result
        } else {
          console.log('error')
        }
      })
    },
    getEngineMode (type) {
      const params = {}
      params.type = type
      appTool.engineMode(params).then(r => {
        if (r.success) {
          this.engineModes = r.result
        } else {
          console.log('error')
        }
      })
    },
    getEngineConfig () {
      const params = {}
      params.engineTypeCode = this.engineTypeCode
      params.engineModeCode = this.engineModeCode
      console.log(params)
      console.log('config s params ', params)
      appTool.engineConfig(params).then(r => {
        if (r.success) {
          console.log('r.result', r.result)
          this.applicationVo.configVo.core.engineConfig.engineConfigs = r.result
        } else {
          console.log('error')
        }
      })
    },
    getFlows () {
      const queryFlowParams = {
        disableMark: 0,
        pageNo: 1,
        pageSize: 99999,
        sortField: 'createTime',
        sortOrder: 'descend'
      }
      flowTool.flowList(queryFlowParams).then(r => {
        if (r.success) {
          this.flowList = r.result.data
        } else {
        }
      })
    },
    handleSelectChange (value) {
      console.log(value)
      this.form.setFieldsValue({
        note: `Hi, ${value === 'male' ? 'man' : 'lady'}!`
      })
    },
    onEnableSpeedChange (switched) {
      this.otherConfigTabs.showSpeedLimitTable = switched
      this.applicationVo.configVo.core.speedLimiter.enableLimiter = switched
      console.log('showSpeedLimitTable', switched)
    },
    onEnableUnresolvedChange (switched) {
      this.otherConfigTabs.showUnresolvedTable = switched
      this.applicationVo.configVo.core.unresolvedCollector.enableCollector = switched
      console.log('showUnresolvedTable', switched)
    },
    otherEnabelChange (switched) {
      console.log('switched', switched)
    },
    otherConfigTabsChange (key) {
      console.log(key)
      this.otherConfigTabs.currentTab = key
    },
    handleCustomVarsChange (data) {
      console.log('tttttttttttttttttttttt', data)
      const newVars = {}
      if (data) {
        data.forEach(item => {
          const newVar = {
            description: item.description,
            key: item.key,
            value: item.value
          }
          newVars[item.key] = newVar
        })
        this.applicationVo.configVo.custom = newVars
      }
    },
    appScheduleChange (value) {
      if (value === 1) {
        this.appTimerValidateInfo.startCron.status = true
        this.applicationVo.timerVo.enableStartTimer = !this.applicationVo.timerVo.enableStartTimer
      } else if (value === 2) {
        this.appTimerValidateInfo.stopCron.status = true
        this.applicationVo.timerVo.enableStopTimer = !this.applicationVo.timerVo.enableStopTimer
      } else if (value === 3) {
        this.appTimerValidateInfo.restartCron.status = true
        this.applicationVo.timerVo.enableRestartTimer = !this.applicationVo.timerVo.enableRestartTimer
      }
    },
    handleAppTimerCronChange (index, value) {
      console.log('cron change event index', index)
      console.log('cron change event', value)
      if (index === 1) {
        this.applicationVo.timerVo.startTimerCron = value
      } else if (index === 2) {
        this.applicationVo.timerVo.stopTimerCron = value
      } else if (index === 3) {
        this.applicationVo.timerVo.restartTimerCron = value
      }
    },
    handleStartTimerCheck (value, message) {
      console.log('handleStartTimerCheck', value)
      console.log('handleStartTimermessage', message)
      this.appTimerValidateInfo.startCron.status = value
      this.appTimerValidateInfo.startCron.message = message
    },
    handleStopTimerCheck (value, message) {
      console.log('handleStopTimerCheck', value)
      console.log('handleStopTimermessage', message)
      this.appTimerValidateInfo.stopCron.status = value
      this.appTimerValidateInfo.stopCron.message = message
    },
    handleRestartTimerCheck (value, message) {
      console.log('handleRestartTimerCheck', value)
      console.log('handleRestartTimermessage', message)
      this.appTimerValidateInfo.restartCron.status = value
      this.appTimerValidateInfo.restartCron.message = message
    }
  }
}
</script>

<style lang="less" scoped>
    .marginTop10 {
      margin-top: 10px;
    }
    .dividerStyle {
      margin-top: 10px;
      margin-bottom: 0;
    }
    .marginNo {
      margin: 0;
    }

   .ApplicationCreate .ant-collapse-icon-position-right > .ant-collapse-item > .ant-collapse-header {
     padding: 5px 16px !important;

   }
   .ApplicationCreate .ant-collapse {
     margin-top: 4px;
   }
   /deep/ .other-config-tab {
       .ant-tabs-bar {
           .ant-tabs-nav-container {
               .ant-tabs-nav-wrap {
                   .ant-tabs-nav-scroll {
                       .ant-tabs-nav {
                           .ant-tabs-tab-active .ant-tabs-tab {
                                font-weight: 700;
                                font-size: inherit
                           }
                       }
                   }
               }
           }
       }
   }
</style>
