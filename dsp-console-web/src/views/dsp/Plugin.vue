<template>
  <a-card :bordered="true" title="Total Plugins">
    <div slot="extra">
      <a-col>
        <a-button
          style="float: right"
          type="primary"
          icon="plus"
          v-action:plugin:create
          @click="uploadPlugin"
        >{{ $t('form.upload') }}</a-button>
      </a-col>
    </div>
    <div id="plugin-div">
      <a-row style="margin-top: 0px">
        <a-col>
          <a-table
            rowKey="id"
            :columns="columns"
            :pagination="pagination"
            :data-source="data"
            @change="handleTableChange"
          >
            <span slot="pluginJarOriginTags" slot-scope="origin">
              <a-tag v-if="origin.code == 1" class="origin-system-tag">{{ $t('plugin.pluginJarOrigin.system') }}</a-tag>
              <a-tag v-if="origin.code == 2" class="origin-user-tag">{{ $t('plugin.pluginJarOrigin.user') }}</a-tag>
            </span>
            <span slot="disableMarkTags" slot-scope="tags">
              <a-tag v-if="tags == 0" class="enable-tag">{{ $t('form.enable') }}</a-tag>
              <a-tag v-if="tags == 1" class="disable-tag">{{ $t('form.disable') }}</a-tag>
            </span>
            <span slot="pluginNumberTags" slot-scope="plugins">
              <a-popover title="Plugins">
                <a-tag color="#1890ff" class="plugin-num-tag">
                  {{ plugins.length }}
                </a-tag>
                <template slot="content">
                  <li v-for="(plu, index) in plugins" :key="plu.pluginClass" style="color: #40a9ff; font-weight: 500; margin: 0px 0px 0px 0px">
                    {{ index + 1 }}. {{ plu.pluginName }}
                    <a-divider v-if="plugins.length !== 1" style="margin: 3px 0px 3px 0px"/>
                  </li>
                </template>
              </a-popover>
            </span>
            <span slot="action" slot-scope="text, record">
              <a-button-group>
                <a-tooltip placement="bottom" :title="$t('form.view')">
                  <a-button v-action:plugin:update @click="showPluginInfo(record)" shape="circle" type="link">
                    <b-icon-eye-fill style="width: 22px; height: 22px"> </b-icon-eye-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('form.disable')">
                  <a-button v-if="record.disableMark==0" v-action:plugin:disable @click="pluginDisable(record)" shape="circle" type="link" >
                    <b-icon-x-circle-fill style="width: 18px; height: 18px;"></b-icon-x-circle-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('form.enable')">
                  <a-button v-if="record.disableMark==1" v-action:plugin:disable @click="pluginDisable(record)" shape="circle" type="link" >
                    <b-icon-check-circle-fill style="width: 18px; height: 18px;"></b-icon-check-circle-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('form.delete')">
                  <a-popconfirm v-if="record.pluginJarOrigin.code === 2" :title="$t('plugin.deleteConfirm')" :ok-text="$t('form.deleteOk')" :cancel-text="$t('form.deleteCancel')" @confirm="pluginDelete(record)">
                    <a-button v-action:plugin:delete shape="circle" type="link">
                      <b-icon-trash-fill style="width: 18px; height: 18px"></b-icon-trash-fill>
                    </a-button>
                  </a-popconfirm>
                  <a-button v-if="record.pluginJarOrigin.code === 1" :disabled="record.pluginJarOrigin.code === 1" v-action:plugin:delete shape="circle" type="link">
                    <b-icon-trash-fill style="width: 18px; height: 18px"></b-icon-trash-fill>
                  </a-button>
                </a-tooltip>
                <a-tooltip placement="bottom" :title="$t('form.upload')">
                  <a-button :disabled="record.pluginJarOrigin.code === 1" v-action:plugin:upload @click="reloadPlugin(record)" shape="circle" type="link">
                    <b-icon-cloud-upload-fill style="width: 18px; height: 18px"></b-icon-cloud-upload-fill>
                  </a-button>
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
                @pressEnter="() => handlePluginSearch(selectedKeys, confirm, column.dataIndex)"
              />
              <a-button
                type="primary"
                icon="search"
                size="small"
                style="width: 90px; margin-right: 8px"
                @click="() => handlePluginSearch(selectedKeys, confirm, column.dataIndex)"
              >
                {{ $t('form.search') }}
              </a-button>
              <a-button size="small" style="width: 90px" @click="() => handlePluginReset(clearFilters)">
                {{ $t('form.reset') }}
              </a-button>
            </div>
            <a-icon
              slot="filterIcon"
              slot-scope="filtered"
              type="search"
              :style="{ color: filtered ? '#108ee9' : undefined }"
            />
            <slot slot="disableMark" slot-scope="type">
              <a-tag v-if="type.code === 0" class="app-tag" color="#ff9933">{{ type.msg }}</a-tag>
              <a-tag v-if="type.code === 1" class="app-tag" color="#00FF00">{{ type.msg }}</a-tag>
            </slot>
            <a-table
              rowKey="id"
              slot="expandedRowRender"
              slot-scope="record"
              style="margin: 0"
              bordered
              :columns="row_columns"
              :data-source="record.plugins"
              :expandIconAsCell="false"
              :expandIconColumnIndex="-1"
              expandRowByClick
              :pagination="false"
              size="middle">
              <span slot="flowRefsTags" slot-scope="text, record">
                <a-popover title="Flow Refs">
                  <a-tag color="#1890ff" class="flow-ref-num-tag">
                    {{ record.flowPluginRefs.length }}
                  </a-tag>
                  <template slot="content">
                    <li v-for="(ref, index) in record.flowPluginRefs" :key="ref.id" style="color: #1890ff; ; font-weight: 500; margin: 0px 0px 0px 0px">
                      {{ index + 1 }}. {{ ref.flowName }}
                      <a-divider v-if="record.flowPluginRefs.length !== 1" style="margin: 3px 0px 3px 0px"/>
                    </li>
                  </template>
                </a-popover>
              </span>
              <span slot="pluginTypeTags" slot-scope="type">
                <a-tag v-if="type.code == 1" class="input-plugin-tag">{{ $t('plugin.pluginTypes.input') }}</a-tag>
                <a-tag v-if="type.code == 2" class="output-plugin-tag">{{ $t('plugin.pluginTypes.output') }}</a-tag>
                <a-tag v-if="type.code == 3" class="process-plugin-tag">{{ $t('plugin.pluginTypes.process') }}</a-tag>
                <a-tag v-if="type.code == 4" class="split-plugin-tag">{{ $t('plugin.pluginTypes.split') }}</a-tag>
                <a-tag v-if="type.code == 5" class="union-plugin-tag">{{ $t('plugin.pluginTypes.union') }}</a-tag>
                <a-tag v-if="type.code == 6" class="system-plugin-tag">{{ $t('plugin.pluginTypes.system') }}</a-tag>
                <a-tag v-if="type.code == 7" class="unknown-plugin-tag">{{ $t('plugin.pluginTypes.unknown') }}</a-tag>
              </span>
              <a-table
                rowKey="key"
                slot="expandedRowRender"
                slot-scope="pluginOne"
                style="margin: 0"
                :columns="row_plugin_configs_columns"
                :data-source="pluginOne.pluginConfigs ? Object.values(pluginOne.pluginConfigs) : []"
                expandRowByClick
                :pagination="false"
                size="small">
              </a-table>
            </a-table>
          </a-table>
          <a-modal
            :visible="pluginUploadModal"
            :footer="null"
            :dialog-style="{ top: '100px' }"
            @ok="handleUploadModalOk"
            @cancel="handleUploadModalCancel"
            destroyOnClose
            :width="bodyStyle.width"
          >
            <a-steps v-model="uploadCurrentStep" :style="stepStyle" type="navigation" >
              <a-step
                :title="$t('plugin.uploadPlugin')"
                :disabled="uploadCurrentStep == 0 && pluginJarUploadCheck.checkButtonStatus !== 4"
                :description="$t('plugin.uploadDesc')"
              />
              <a-step
                :title="$t('plugin.verifyPlugin')"
                :disabled="uploadCurrentStep == 0 && pluginJarUploadCheck.checkButtonStatus !== 4"
                :description="$t('plugin.verifyDesc')"
              />
            </a-steps>
            <a-card class="steps-content" v-if="uploadCurrentStep==0" :bordered="false" style="padding: 5px;margin-top: 0px; margin-bo: 0px;">
              <a-space direction="vertical" style="width: 100%">
                <a-row type="flex">
                  <a-divider orientation="left">
                    <h3> {{ $t('plugin.stepOneDesc') }} </h3>
                  </a-divider>
                </a-row>
                <a-row >
                  <a-upload-dragger
                    name="file"
                    accept=".jar"
                    :file-list="pluginJarFileList"
                    :multiple="false"
                    :customRequest="uploadPluginJarFile"
                    :remove="deletePluginJarFile"
                  >
                    <p class="ant-upload-drag-icon">
                      <a-icon type="inbox" />
                    </p>
                    <p class="ant-upload-text">
                      {{ $t('plugin.uploadFileTip1') }}
                    </p>
                    <p class="ant-upload-hint">
                      {{ $t('plugin.uploadFileTip2') }}
                    </p>
                  </a-upload-dragger>
                </a-row>
                <a-row style="margin-top: 8px" type="flex">
                  <a-divider orientation="left">
                    <h3>{{ $t('plugin.stepTwoDesc') }}</h3>
                  </a-divider>
                </a-row>
                <a-row >
                  <a-row >
                    <a-alert
                      :message="$t('plugin.stepOneMsg1')"
                      :description="$t('plugin.stepOneMsg2')"
                      type="info"
                      show-icon
                    />
                  </a-row>
                  <a-row>
                    <a-card style="padding: 0px;margin-top: 5px" :bordered="false">
                      <a-row style="padding: 10px; " :gutter="24">
                        <a-row type="flex">
                          <a-col flex="auto">
                            <a-progress
                              :stroke-color="{
                                '0%': '#108ee9',
                                '100%': '#87d068',
                              }"
                              :percent="this.pluginJarUploadCheck.checkProgress"
                              :strokeWidth="12"
                              :status="pluginJarUploadCheck.checkButtonStatus === 2 ? 'active' : (pluginJarUploadCheck.checkButtonStatus === 3 ? 'exception' : (pluginJarUploadCheck.checkButtonStatus === 4 ? 'success' : 'normal'))"
                            />
                          </a-col>
                          <a-col flex="120px">
                            <div :style="{ float: 'right'}">
                              <a-button
                                :disabled="pluginJarUploadCheck.checkButtonStatus === 0"
                                v-if="pluginJarUploadCheck.checkButtonStatus === 0 || pluginJarUploadCheck.checkButtonStatus === 1"
                                class="check-button"
                                :style="{background: '#ffffff'}"
                                type="primary"
                                ghost
                                @click="startPluginCheck(pluginJarUploadCheck.fileInfo)">
                                {{ $t('plugin.verify') }}
                              </a-button>
                              <a-button
                                v-if="pluginJarUploadCheck.checkButtonStatus === 2"
                                class="check-button"
                                type="primary"
                                loading>
                                {{ $t('plugin.verifying') }}
                              </a-button>
                              <a-button
                                v-if="pluginJarUploadCheck.checkButtonStatus === 3"
                                class="check-button"
                                :style="{background: 'red', border: 'red'}"
                                type="primary"
                                @click="startPluginCheck(pluginJarUploadCheck.fileInfo)">
                                {{ $t('plugin.tryAgain') }}
                              </a-button>
                              <a-button
                                v-if="pluginJarUploadCheck.checkButtonStatus === 4"
                                class="check-button"
                                :style="{background: '#52c41a', border: '#52c41a'}"
                                type="primary">
                                {{ $t('plugin.complete') }}
                              </a-button>
                            </div>
                          </a-col>
                        </a-row>
                      </a-row>
                      <a-row style="padding: 5px;">
                        <a-skeleton :paragraph="{ rows: 4 }" v-if="pluginJarUploadCheck.checkButtonStatus === 1 || pluginJarUploadCheck.checkButtonStatus === 0"/>
                        <a-skeleton active :paragraph="{ rows: 4 }" v-if="pluginJarUploadCheck.checkButtonStatus === 2"/>
                        <a-result
                          v-if="pluginJarUploadCheck.checkButtonStatus === 4"
                          status="success"
                          title="Verify Plugin Jar Successfully!"
                          sub-title="Please click next to continue the plugin upload operation"
                        >
                        </a-result>
                        <a-result
                          status="error"
                          v-if="pluginJarUploadCheck.checkButtonStatus === 3"
                          title="Verify Plugin Jar Failed!"
                          :sub-title="pluginJarUploadCheck.checkErrorInfo"
                        >
                        </a-result>
                      </a-row>
                    </a-card>
                  </a-row>
                </a-row>
              </a-space>
            </a-card>
            <a-card class="steps-content" v-if="uploadCurrentStep==1" :bordered="false" style="padding: 5px;margin-top: 0px; margin-bo: 0px;">
              <a-space direction="vertical" style="width: 100%">
                <a-card
                  class="plugin-view-card"
                  :bordered="false"
                  style="padding: 0px; margin-top: 0px; margin-bo: 0px;">
                  <a-row>
                    <a-descriptions
                      :title="$t('plugin.pluginInfo')">
                      <a-descriptions-item :label="$t('plugin.pluginJarName')">
                        {{ pluginJarUploadCheck.checkedJarVo.pluginJarName }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.pluginJarId')">
                        {{ pluginJarUploadCheck.checkedJarVo.pluginJarId }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.pluginJarAlias')">
                        <EditTableCell :text="pluginJarUploadCheck.checkedJarVo.pluginJarAlias" @change="pluginJarCheckDataChange(pluginJarUploadCheck.checkedJarVo, 'pluginJarAlias', $event)" />
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.uploadTime')">
                        {{ this.dateFormat(pluginJarUploadCheck.checkedJarVo.uploadTime) }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.remark')">
                        <EditTableCell :text="pluginJarUploadCheck.checkedJarVo.remarkMsg" @change="pluginJarCheckDataChange(pluginJarUploadCheck.checkedJarVo, 'remarkMsg', $event)" />
                      </a-descriptions-item>
                    </a-descriptions>
                  </a-row>
                  <a-row style="padding-top: 0px; margin-top: 20px">
                    <a-descriptions
                      :title="$t('plugin.extInfo')">
                      <a-descriptions-item :label="$t('plugin.pluginJarMd5')">
                        {{ pluginJarUploadCheck.checkedJarVo.pluginJarMd5 }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.fileSize')">
                        {{ pluginJarUploadCheck.checkedJarVo.fileSize }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.disableMark')">
                        <!-- <a-badge v-if="pluginJarUploadCheck.checkedJarVo.disableMark === 0" status="success" :text="$t('form.enable')" />
                        <a-badge v-if="pluginJarUploadCheck.checkedJarVo.disableMark === 1" status="error" :text="$t('form.disable')" /> -->
                        <a-switch :v-model="pluginJarUploadCheck.checkedJarVo.disableMark === 1 ? true : false" @change="pluginJarCheckedDisable"/>
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.pluginDefFile')">
                        {{ pluginJarUploadCheck.checkedJarVo.pluginDefFile }}
                      </a-descriptions-item>
                      <a-descriptions-item :label="$t('plugin.pluginDefFileContent')">
                        <a @click="openPluginDefCollapse(pluginJarUploadCheck.checkedJarVo.pluginDefFileContent)">{{ $t('form.view') }}</a>
                      </a-descriptions-item>
                    </a-descriptions>
                  </a-row>
                  <a-row style="padding-top: 0px; margin-top: 20px">
                    <a-descriptions
                      :title="$t('plugin.pluginList')">
                      <a-descriptions-item>
                        <a-table
                          rowKey="pluginClass"
                          :columns="plugin_check_list_columns"
                          :data-source="pluginJarUploadCheck.checkedJarVo.plugins"
                          :pagination="false"
                          :scroll="{ x: false, y: 300 }">
                          <EditTableCell slot="plugin-alias-edit-coll" slot-scope="text, record" :text="record.pluginAlias" @change="pluginCheckDataChange(record, 'pluginAlias', $event)">
                          </EditTableCell>
                          <EditTableCell slot="plugin-desc-edit-coll" slot-scope="text, record" :text="record.pluginDescription" @change="pluginCheckDataChange(record, 'pluginDescription', $event)">
                          </EditTableCell>
                        </a-table>
                      </a-descriptions-item>
                    </a-descriptions>
                  </a-row>
                  <a-drawer
                    width="600"
                    placement="right"
                    :closable="true"
                    :destroyOnClose="true"
                    :mask="true"
                    :maskClosable="true"
                    :keyboard="true"
                    :title="$t('plugin.pluginDefFileContent')"
                    :visible="pluginDefFileContentVisible"
                    @close="closePluginDefCollapse">
                    <pre v-highlightjs="pluginDefFileContent"><code></code></pre>
                  </a-drawer>
                </a-card>
              </a-space>
            </a-card>
            <div class="steps-action">
              <a-button v-if="uploadCurrentStep > 0" @click="prevUploadStep">
                {{ $t('plugin.previous') }}
              </a-button>
              <a-button
                v-if="uploadCurrentStep == 1"
                type="primary"
                style="margin-left: 8px"
                v-action:plugin:create
                @click="submitUploadStep"
              >
                {{ $t('plugin.finish') }}
              </a-button>
              <a-button v-if="uploadCurrentStep < 1" :disabled="pluginJarUploadCheck.checkButtonStatus !== 4" style="margin-left: 8px" type="primary" @click="nextUploadStep">
                {{ $t('plugin.next') }}
              </a-button>
            </div>
          </a-modal>
          <a-modal
            :visible="pluginViewModal"
            :footer="null"
            :dialog-style="{ top: '100px' }"
            @ok="handleViewModalOk"
            @cancel="handleViewModalCancel"
            destroyOnClose
            :width="bodyStyle.width">
            <a-card
              class="plugin-view-card"
              :bordered="false"
              :title="$t('plugin.pluginDetailInfo')"
              style="padding: 5px; margin-top: 0px; margin-bo: 0px;">
              <a-row>
                <a-descriptions
                  :title="$t('plugin.pluginInfo')">
                  <a-descriptions-item :label="$t('plugin.pluginJarName')">
                    {{ viewModalData.pluginJarName }}
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.pluginJarId')">
                    {{ viewModalData.pluginJarId }}
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.pluginJarAlias')">
                    <EditTableCell :text="viewModalData.pluginJarAlias" @change="pluginViewEditCellChange(viewModalData, 'pluginJarAlias', $event)" />
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.createTime')">
                    {{ this.dateFormat(viewModalData.createTime) }}
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.updateTime')">
                    {{ this.dateFormat(viewModalData.updateTime) }}
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.uploadTime')">
                    {{ this.dateFormat(viewModalData.uploadTime) }}
                  </a-descriptions-item>
                </a-descriptions>
              </a-row>
              <a-row style="padding-top: 0px; margin-top: 20px">
                <a-descriptions
                  :title="$t('plugin.extInfo')">
                  <a-descriptions-item :label="$t('plugin.pluginJarMd5')">
                    {{ viewModalData.pluginJarMd5 }}
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.fileSize')">
                    {{ viewModalData.fileSize }}
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.disableMark')">
                    <a-badge v-if="viewModalData.disableMark === 0" status="success" :text="$t('form.enable')" />
                    <a-badge v-if="viewModalData.disableMark === 1" status="error" :text="$t('form.disable')" />
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.pluginDefFile')">
                    {{ viewModalData.pluginDefFile }}
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.pluginDefFileContent')">
                    <a @click="openPluginDefCollapse(viewModalData.pluginDefFileContent)">{{ $t('form.view') }}</a>
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.remark')">
                    <EditTableCell v-action:plugin:update :text="viewModalData.remarkMsg" @change="pluginViewEditCellChange(viewModalData, 'remarkMsg', $event)" />
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('plugin.pluginJarUrl')">
                    {{ viewModalData.pluginJarUrl }}
                  </a-descriptions-item>
                </a-descriptions>
              </a-row>
              <a-row style="padding-top: 0px; margin-top: 20px">
                <a-descriptions
                  :title="$t('plugin.pluginList')">
                  <a-descriptions-item>
                    <a-table
                      rowKey="pluginId"
                      :columns="plugin_view_list_columns"
                      :data-source="viewModalData.plugins"
                      :pagination="false"
                      :scroll="{ x: false, y: 300 }"
                      :bordered="true">
                      <EditTableCell slot="plugin-alias-edit-coll" slot-scope="text, record" :text="record.pluginAlias" @change="pluginViewDataChange(record, 'pluginAlias', $event)">
                      </EditTableCell>
                      <EditTableCell slot="plugin-desc-edit-coll" slot-scope="text, record" :text="record.pluginDescription" @change="pluginViewDataChange(record, 'pluginDescription', $event)">
                      </EditTableCell>
                      <span slot="pluginTypeTags" slot-scope="type">
                        <a-tag v-if="type.code == 1" class="input-plugin-tag">{{ $t('plugin.pluginTypes.input') }}</a-tag>
                        <a-tag v-if="type.code == 2" class="output-plugin-tag">{{ $t('plugin.pluginTypes.output') }}</a-tag>
                        <a-tag v-if="type.code == 3" class="process-plugin-tag">{{ $t('plugin.pluginTypes.process') }}</a-tag>
                        <a-tag v-if="type.code == 4" class="split-plugin-tag">{{ $t('plugin.pluginTypes.split') }}</a-tag>
                        <a-tag v-if="type.code == 5" class="union-plugin-tag">{{ $t('plugin.pluginTypes.union') }}</a-tag>
                        <a-tag v-if="type.code == 6" class="system-plugin-tag">{{ $t('plugin.pluginTypes.system') }}</a-tag>
                        <a-tag v-if="type.code == 7" class="unknown-plugin-tag">{{ $t('plugin.pluginTypes.unknown') }}</a-tag>
                      </span>
                    </a-table>
                  </a-descriptions-item>
                </a-descriptions>
              </a-row>
              <a-drawer
                width="600"
                placement="right"
                :closable="true"
                :destroyOnClose="true"
                :mask="true"
                :maskClosable="true"
                :keyboard="true"
                :title="$t('plugin.pluginDefFileContent')"
                :visible="pluginDefFileContentVisible"
                @close="closePluginDefCollapse">
                <pre v-highlightjs="pluginDefFileContent"><code></code></pre>
              </a-drawer>
            </a-card>
          </a-modal>
        </a-col>
      </a-row>
    </div>
  </a-card>
</template>

<script>

import * as pluginTool from '@/api/plugin'
import moment from 'moment'
import CountTo from 'vue-count-to'
import EditTableCell from '@/components/EditTableCell'
function sleep (ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}
export default {
  name: 'Plugin',
  components: {
    CountTo,
    moment,
    EditTableCell
  },
  computed: {
    plugin_view_list_columns () {
      const columns = [
        {
          title: this.$t('plugin.pluginName'),
          width: '20%',
          dataIndex: 'pluginName',
          key: 'pluginName',
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginAlias'),
          width: '20%',
          dataIndex: 'pluginAlias',
          key: 'pluginAlias',
          scopedSlots: {
            customRender: 'plugin-alias-edit-coll'
          },
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginType'),
          width: '10%',
          dataIndex: 'pluginType',
          key: 'pluginType',
          scopedSlots: {
            customRender: 'pluginTypeTags'
          }
        },
        {
          title: this.$t('plugin.pluginClass'),
          width: '30%',
          dataIndex: 'pluginClass',
          key: 'pluginClass',
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginDescription'),
          width: '20%',
          dataIndex: 'pluginDescription',
          key: 'pluginDescription',
          scopedSlots: {
            customRender: 'plugin-desc-edit-coll'
          }
        }
      ]
      return columns
    },
    plugin_check_list_columns () {
      const columns = [
        {
          title: this.$t('plugin.pluginName'),
          width: '15%',
          dataIndex: 'pluginName',
          key: 'pluginName',
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginAlias'),
          width: '15%',
          dataIndex: 'pluginAlias',
          key: 'pluginAlias',
          scopedSlots: {
            customRender: 'plugin-alias-edit-coll'
          },
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginType'),
          width: '10%',
          dataIndex: 'pluginType',
          key: 'pluginType'
        },
        {
          title: this.$t('plugin.pluginClass'),
          width: '25%',
          dataIndex: 'pluginClass',
          key: 'pluginClass',
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginDescription'),
          width: '25%',
          dataIndex: 'pluginDescription',
          key: 'pluginDescription',
          scopedSlots: {
            customRender: 'plugin-desc-edit-coll'
          }
        }
      ]
      return columns
    },
    row_columns () {
      const columns = [
        {
          title: this.$t('plugin.pluginName'),
          dataIndex: 'pluginName',
          key: 'pluginName',
          width: '15%',
          align: 'center',
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginClass'),
          dataIndex: 'pluginClass',
          key: 'pluginClass',
          width: '20%',
          align: 'center',
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginType'),
          dataIndex: 'pluginType',
          key: 'pluginType',
          width: '15%',
          align: 'center',
          scopedSlots: {
            customRender: 'pluginTypeTags'
          }
        },
        {
          title: this.$t('plugin.flowRef'),
          dataIndex: 'flowRef',
          key: 'flowRef',
          width: '15%',
          align: 'center',
          scopedSlots: {
            customRender: 'flowRefsTags'
          }
        },
        {
          title: this.$t('plugin.pluginDescription'),
          dataIndex: 'pluginDescription',
          key: 'pluginDescription',
          width: '20',
          align: 'center',
          ellipsis: true
        },
        {
          title: this.$t('plugin.updateTime'),
          dataIndex: 'updateTime',
          key: 'updateTime',
          width: '15%',
          align: 'center',
          customRender: (text, row, index) => {
            if (text) {
              return moment(text).format('YYYY-MM-DD HH:mm:ss')
            } else {
              return '-'
            }
          }
        }
      ]
      return columns
    },
    row_plugin_configs_columns () {
      const columns = [
        {
          title: this.$t('plugin.pluginConfigs.key'),
          dataIndex: 'key',
          key: 'key',
          width: '20%',
          align: 'center'
        },
        {
          title: this.$t('plugin.pluginConfigs.type'),
          dataIndex: 'type',
          key: 'type',
          width: '20%',
          align: 'center'
        },
        {
          title: this.$t('plugin.pluginConfigs.defaultValue'),
          dataIndex: 'defaultValue',
          key: 'defaultValue',
          width: '20%',
          align: 'center',
          customRender: (text, row, index) => {
            if (text) {
              if (text instanceof Array) {
                return '[' + String(text) + ']'
              }
              if (text instanceof Boolean) {
                return String(text)
              }
            } else {
              if (row.type) {
                if (!row.type.indexOf('[')) {
                  return '[]'
                }
                if (!row.type.indexOf('boolean') || !row.type.indexOf('Boolean')) {
                  return 'false'
                }
              }
            }
            if (text === undefined) {
              return ''
            }
            return String(text) + ''
          }
        },
        {
          title: this.$t('plugin.pluginConfigs.required'),
          dataIndex: 'required',
          key: 'required',
          width: '20%',
          align: 'center',
          customRender: (text, row, index) => {
            if (text && text.type instanceof Boolean) {
              if (text) {
                return 'true'
              } else {
                return 'false'
              }
            }
            return text + ''
          }
        },
        {
          title: this.$t('plugin.pluginConfigs.description'),
          dataIndex: 'description',
          key: 'description',
          width: '20%',
          align: 'center',
          ellipsis: true
        }
      ]
      return columns
    },
    columns () {
      const columns = [
        {
          title: this.$t('plugin.pluginJarName'),
          dataIndex: 'pluginJarName',
          key: 'pluginJarName',
          width: '15%',
          scopedSlots: {
            filterDropdown: 'filterDropdown',
            filterIcon: 'filterIcon'
          },
          align: 'center',
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginJarId'),
          key: 'pluginJarId',
          dataIndex: 'pluginJarId',
          width: '15%',
          scopedSlots: {
            filterDropdown: 'filterDropdown',
            filterIcon: 'filterIcon'
          },
          align: 'center'
        },
        {
          title: this.$t('plugin.pluginJarAlias'),
          dataIndex: 'pluginJarAlias',
          key: 'pluginJarAlias',
          width: '10%',
          scopedSlots: {
            filterDropdown: 'filterDropdown',
            filterIcon: 'filterIcon'
          },
          align: 'center',
          ellipsis: true
        },
        {
          title: this.$t('plugin.pluginJarOrigin.title'),
          key: 'pluginJarOrigin',
          dataIndex: 'pluginJarOrigin',
          width: '10%',
          filters: [
            { text: this.$t('plugin.pluginJarOrigin.system'), value: 1 },
            { text: this.$t('plugin.pluginJarOrigin.user'), value: 2 }
          ],
          filterMultiple: false,
          scopedSlots: {
            customRender: 'pluginJarOriginTags'
          },
          align: 'center'
        },
        {
          title: this.$t('plugin.disableMark'),
          key: 'disableMark',
          dataIndex: 'disableMark',
          width: '10%',
          filters: [
            { text: this.$t('form.enable'), value: 0 },
            { text: this.$t('form.disable'), value: 1 }
          ],
          filterMultiple: false,
          scopedSlots: {
            customRender: 'disableMarkTags'
          },
          align: 'center'
        },
        {
          title: this.$t('plugin.pluginNum'),
          dataIndex: 'plugins',
          key: 'pluginNum',
          width: '10%',
          scopedSlots: {
            customRender: 'pluginNumberTags'
          },
          align: 'center'
        },
        {
          title: this.$t('plugin.createTime'),
          key: 'createTime',
          dataIndex: 'createTime',
          width: '10%',
          sorter: true,
          customRender: (text, row, index) => {
            return moment(text).format('YYYY-MM-DD HH:mm:ss')
          },
          align: 'center'
        },
        { title: this.$t('plugin.remark'),
          key: 'remarkMsg',
          dataIndex: 'remarkMsg',
          width: '10%',
          align: 'center',
          ellipsis: true
        },
        { title: this.$t('form.action'),
          key: 'action',
          width: '10%',
          scopedSlots: {
            customRender: 'action'
          },
          align: 'center'
        }
      ]
      return columns
    }
  },
  data () {
    return {
      viewModalData: {},
      uploadModalData: {},
      pluginDefFileContentVisible: false,
      pluginDefFileContent: '',
      pluginJarFileList: [],
      pluginJarUploadCheck: {
        checkButtonStatus: 0,
        checkProgress: 0,
        fileInfo: {},
        checkErrorInfo: 'Verification failed. Please check whether the plugin jar meets the specification and upload it again',
        checkedJarVo: {}
      },
      bodyStyle: {
        height: '600px',
        width: '800px'
      },
      uploadCurrentStep: 0,
      stepStyle: {
        marginBottom: '20px',
        boxShadow: '0px -1px 0 0 #e8e8e8 inset'
      },
      pluginUploadModal: false,
      pluginViewModal: false,
      pagination: {
        total: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100']
      },
      pluginQuery: {
        pluginId: undefined,
        pluginJarAlias: undefined,
        pluginJarName: undefined,
        pluginJarOrigin: undefined,
        disableMark: undefined,
        sortField: 'createTime',
        sortOrder: 'descend',
        pageNo: 1,
        pageSize: 10
      },
      pluginTypes: [],
      disableTypes: [
        {
          code: 0,
          msg: '启用'
        },
        {
          code: 1,
          msg: '禁用'
        }
      ],
      data: []
    }
  },
  created () {
    this.getPluginTypes()
    this.pluginList(this.pluginQuery)
  },
  mounted () {
    this.searchFormWidth() // 组件初始化的时候不会触发onresize事件，这里强制执行一次
    window.onresize = () => {
      if (!this.timer) { // 使用节流机制，降低函数被触发的频率
        this.timer = true
        const that = this // 匿名函数的执行环境具有全局性，为防止this丢失这里用that变量保存一下
        setTimeout(function () {
          that.searchFormWidth()
          that.timer = false
        }, 400)
      }
    }
  },
  destroyed () {
    window.onresize = null
  },
  beforeDestroy () {
    clearInterval(this.appTimer)
  },
  methods: {
    pluginViewDataChange (record, key, text) {
      if (key && key === 'pluginDescription') {
        record.pluginDescription = text
      }
      if (key && key === 'pluginAlias') {
        record.pluginAlias = text
      }
      console.log('record:', record)
      pluginTool.pluginUpdatePart(this.viewModalData).then((r) => {
        if (r.success) {
          console.log('success')
        } else {
          console.log('error')
        }
      })
    },
    pluginViewEditCellChange (record, key, text) {
      if (key && key === 'remarkMsg') {
        record.remarkMsg = text
      }
      if (key && key === 'pluginJarAlias') {
        record.pluginJarAlias = text
      }
      console.log('record:', record)
      pluginTool.pluginUpdatePart(record).then((r) => {
        if (r.success) {
          console.log('success')
        } else {
          console.log('error')
        }
      })
    },
    pluginJarCheckedDisable (checked) {
      if (checked) {
        this.pluginJarUploadCheck.checkedJarVo.disableMark = 1
      } else {
        this.pluginJarUploadCheck.checkedJarVo.disableMark = 0
      }
    },
    pluginCheckDataChange (record, key, value) {
      console.log('pluginCheckDataChange', key, record, value)
      if (value) {
        if (key === 'pluginDescription') {
          record.pluginDescription = value
          console.log('record', record)
          console.log('aaaaaa', this.pluginJarUploadCheck.checkedJarVo)
        }
        if (key === 'pluginAlias') {
          record.pluginAlias = value
          console.log('record', record)
          console.log('aaaaaa', this.pluginJarUploadCheck.checkedJarVo)
        }
      }
    },
    pluginJarCheckDataChange (record, key, value) {
      console.log('pluginJarCheckDataChange', key, record, value)
      if (value) {
        console.log('value-------------------', value)
        if (key === 'remarkMsg') {
          record.remarkMsg = value
          console.log('record', record)
          console.log('aaaaaa', this.pluginJarUploadCheck.checkedJarVo)
        }
        if (key === 'pluginJarAlias') {
          record.pluginJarAlias = value
          console.log('record', record)
          console.log('aaaaaa', this.pluginJarUploadCheck.checkedJarVo)
        }
      }
    },
    dateFormat: function (date) {
      if (date) {
        return moment(date).format('YYYY-MM-DD HH:mm:ss')
      }
      return '-'
    },
    pluginList (params) {
      console.log('params: ', params)
      pluginTool.pluginList(params).then((r) => {
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
    async uploadPluginJarFile (info) {
      // 修改校验按钮状态变为不可点击
      this.pluginJarUploadCheck.checkButtonStatus = 0
      // 修改校验进度条为0
      this.pluginJarUploadCheck.checkProgress = 0
      // 定义文件对象
      const fileInfo = {
        pluginJarId: undefined,
        uid: info.file.uid,
        name: info.file.name,
        status: 'uploading',
        response: '',
        url: ''
      }
      // 把文件对象保存到文件列表中,用于显示页面
      this.pluginJarFileList.push(fileInfo)
      // 如果文件列表长度为2, 则删去前一个文件
      if (this.pluginJarFileList.length === 2) {
        this.pluginJarFileList = this.pluginJarFileList.slice(-1)
      };
      // 文件上传参数
      const formData = new FormData()
      // 如果是reload(pluginJarId不为空),就把jarId添加到formData中, 否则为upload, 就将pluginJarId设置为-1
      if (this.uploadModalData.pluginJarId) {
        formData.append('pluginJarId', this.uploadModalData.pluginJarId)
      } else {
        formData.append('pluginJarId', '-1')
      };
      // 将上传文件的二进制对象添加到formData
      formData.append('file', info.file)
      // 调用api上传文件
      pluginTool.pluginUpload(formData).then((r) => {
        const res = r.result
        if (r.success) {
          this.$message.success({
            content: this.$t('plugin.notifyFileUploadSuccess')
          })
          // 将后台返回的res对象参数保存到fileinfo的参数中
          fileInfo.status = res.status
          fileInfo.response = res.response
          fileInfo.url = res.url
          fileInfo.pluginJarId = res.pluginJarId
          // 上传成功的文件保存到check对象,等待check
          this.pluginJarUploadCheck.fileInfo = fileInfo
          // 修改校验按钮变为可以点击的状态
          this.pluginJarUploadCheck.checkButtonStatus = 1
          // reload情况下,如果上传文件的名称与原记录不同,则给出警告
          if (this.uploadModalData.pluginJarId) {
            if (fileInfo.name !== this.uploadModalData.pluginJarName) {
              this.$message.warning(this.$t('plugin.reloadWarn'), 3)
            }
          }
        } else {
          this.$message.error({
            content: this.$t('plugin.notifyFileUploadFail')
          })
          fileInfo.status = 'exception'
          fileInfo.response = this.$t('plugin.notifyFileUploadFail')
        }
      })
    },
    deletePluginJarFile (file) {
      const index = this.pluginJarFileList.indexOf(file)
      this.pluginJarFileList.splice(index, 1)
      this.pluginJarUploadCheck.checkButtonStatus = 0
      this.pluginJarUploadCheck.checkProgress = 0
      return true
    },

    getPluginTypes () {
      pluginTool.pluginTypes().then((r) => {
        if (r.success) {
          this.pluginTypes = r.result
        }
      })
    },
    nextUploadStep () {
      this.uploadCurrentStep++
    },
    prevUploadStep () {
      this.uploadCurrentStep--
    },
    submitUploadStep () {
      console.log('this.pluginJarUploadCheck.checkedJarVo', this.pluginJarUploadCheck.checkedJarVo)
      if (this.uploadModalData.pluginJarId) {
        pluginTool.pluginUpdate(this.pluginJarUploadCheck.checkedJarVo).then((r) => {
          if (r.success) {
            console.log('插件重新加载成功', r)
            this.pluginList(this.pluginQuery)
            this.handleUploadModalCancel('')
            this.$notification.success({
              message: this.$t('message.success'),
              description: this.$t('plugin.notifyReloadSuccess')
            })
          } else {
            console.log('error')
            this.$notification.error({
              message: this.$t('message.failed'),
              description: r.message || '',
              duration: 5
            })
          }
        })
      } else {
        pluginTool.pluginCreate(this.pluginJarUploadCheck.checkedJarVo).then((r) => {
          if (r.success) {
            console.log('插件上传成功', r)
            this.pluginList(this.pluginQuery)
            this.handleUploadModalCancel('')
            this.$notification.success({
              message: this.$t('message.success'),
              description: this.$t('plugin.notifyUploadSuccess')
            })
          } else {
            console.log('error')
            this.$notification.error({
              message: this.$t('message.failed'),
              description: r.message || '',
              duration: 5
            })
          }
        })
      }
    },
    handlePluginSearch (selectedKeys, confirm, dataIndex) {
      confirm()
    },
    handlePluginReset (clearFilters) {
      clearFilters()
    },
    handleTableChange (pagination, filters, sorter) {
      if (filters.pluginJarId) {
        this.pluginQuery.pluginJarId = filters.pluginJarId[0]
      }
      if (filters.pluginJarAlias) {
        this.pluginQuery.pluginJarAlias = filters.pluginJarAlias[0]
      }
      if (filters.pluginJarName) {
        this.pluginQuery.pluginJarName = filters.pluginJarName[0]
      }
      if (filters.disableMark) {
        this.pluginQuery.disableMark = filters.disableMark[0]
      }
      if (filters.pluginJarOrigin) {
        this.pluginQuery.pluginJarOrigin = filters.pluginJarOrigin[0]
      }
      this.pluginQuery.pageNo = pagination.current
      this.pluginQuery.pageSize = pagination.pageSize
      this.pluginQuery.sortField = sorter.field
      this.pluginQuery.sortOrder = sorter.order
      console.log('pagination', pagination)
      console.log('filters', filters)
      console.log('sorter', sorter)
      console.log('pluginQuery', this.pluginQuery)
      this.pluginList(this.pluginQuery)
    },
    uploadPlugin () {
      this.pluginJarUploadCheck = {
        checkButtonStatus: 0,
        checkProgress: 0,
        fileInfo: {},
        checkErrorInfo: 'Verification failed. Please check whether the plugin jar meets the specification and upload it again',
        checkedJarVo: {}
      }
      this.pluginUploadModal = true
    },
    reloadPlugin (record) {
      console.log('record', record)
      this.uploadModalData = record
      this.pluginJarUploadCheck.fileInfo = {}
      this.pluginJarUploadCheck.checkButtonStatus = 0
      this.pluginJarUploadCheck.checkProgress = 0
      this.uploadCurrentStep = 0
      this.pluginUploadModal = true
    },
    showPluginInfo (record) {
      console.log('record', record)
      this.viewModalData = record
      this.pluginViewModal = true
    },
    pluginDisable (record) {
      console.log('record', record)
      pluginTool.pluginDisable(record).then((r) => {
        if (r.success) {
          record.disableMark = record.disableMark === 0 ? 1 : 0
          this.$notification['success']({
            message: this.$t('message.success'),
            description: record.disableMark === 1 ? this.$t('message.plugin.disable') : this.$t('message.plugin.enable'),
            duration: 5
          })
        } else {
          console.log('error')
          this.$notification['error']({
            message: this.$t('message.failed'),
            description: r.message || '',
            duration: 5
          })
        }
      })
    },
    pluginDelete (record) {
      console.log('record', record)
      pluginTool.pluginDelete(record).then((r) => {
        if (r.success) {
          this.$notification.success({
            message: this.$t('message.success'),
            description: this.$t('message.plugin.delete_success')
          })
          setTimeout(() => {
            this.pluginList(this.pluginQuery)
          }, 1000)
        } else {
          console.log('error')
          this.$notification['error']({
            message: this.$t('message.failed'),
            description: r.message || '',
            duration: 5
          })
        }
      })
    },
    handleUploadModalOk (e) {
      this.pluginJarFileList = []
      this.pluginJarUploadCheck.fileInfo = {}
      this.pluginJarUploadCheck.checkProgress = 0
      this.pluginJarUploadCheck.checkButtonStatus = 0
      this.uploadCurrentStep = 0
      this.uploadModalData = {}
      this.pluginUploadModal = false
    },
    handleUploadModalCancel (e) {
      this.pluginJarFileList = []
      this.pluginJarUploadCheck.fileInfo = {}
      this.pluginJarUploadCheck.checkProgress = 0
      this.pluginJarUploadCheck.checkButtonStatus = 0
      this.uploadCurrentStep = 0
      this.uploadModalData = {}
      this.pluginUploadModal = false
    },
    async changeCheckProgress () {
      await sleep(1000)
      while (this.pluginJarUploadCheck.checkButtonStatus === 2) {
        var rnum = Math.floor(Math.random() * (15 - 0 + 1)) + 0
        const p = this.pluginJarUploadCheck.checkProgress + rnum
        if (p <= 95) {
          this.pluginJarUploadCheck.checkProgress = p
        } else {
          this.pluginJarUploadCheck.checkProgress = 95
          if (this.pluginJarUploadCheck.checkButtonStatus === 4) {
            this.pluginJarUploadCheck.checkProgress = 100
          }
        }
        console.log('等待1s')
        await sleep(500)
      }
    },
    async startPluginCheck (fileInfo) {
      this.pluginJarUploadCheck.checkErrorInfo = 'Verification failed. Please check whether the plugin jar meets the specification and upload it again'
      // 设置校验按钮变为校验中
      this.pluginJarUploadCheck.checkButtonStatus = 2
      // 设置校验进度条为重新开始
      this.pluginJarUploadCheck.checkProgress = 0
      // 模拟进度条增长,最大95%,等待后台返回
      this.changeCheckProgress()
      const that = this
      // 校验最大45秒超时,超时后设置校验按钮为重试
      setTimeout(function () {
        if (that.pluginJarUploadCheck.checkButtonStatus === 2) { that.pluginJarUploadCheck.checkButtonStatus = 3 }
      }, 45000)
      await sleep(2000)
      // 请求后台接口
      pluginTool.pluginVerify(fileInfo).then((r) => {
        if (r.success) {
          // 设置校验按钮状态和进度条
          this.pluginJarUploadCheck.checkButtonStatus = 4
          this.pluginJarUploadCheck.checkProgress = 100
          this.pluginJarUploadCheck.checkedJarVo = r.result
        } else {
          // 设置按钮为失败
          this.pluginJarUploadCheck.checkButtonStatus = 3
          this.pluginJarUploadCheck.checkErrorInfo = r.message
        }
      })
    },
    handleViewModalOk (e) {
      this.pluginViewModal = false
    },
    handleViewModalCancel (e) {
      this.pluginViewModal = false
      this.viewModalData = {}
    },
    openPluginDefCollapse (content) {
      this.pluginDefFileContent = content
      this.pluginDefFileContentVisible = true
    },
    closePluginDefCollapse () {
      this.pluginDefFileContent = ''
      this.pluginDefFileContentVisible = false
    },
    searchFormWidth () {
      const w = window.innerWidth
      const h = window.innerHeight
      this.bodyStyle.width = (w * 0.65) + 'px'
      this.bodyStyle.height = (h * 0.85) + 'px'
    }
  }
}
</script>

<style lang="less" scoped>
#app_table_head_class {
  font-weight: bold;
  font-size: 16px;
  margin-left: 5px;
  padding: auto;
}
.CountToNum {
  margin-left: 5px;
}
.app-tag {
  width: 50px;
  text-align: center;
  border-radius: 0;
  font-weight: 700;
  font-size: 12px;
  text-align: center;
  padding: 0 4px;
  margin-right: 0px;
  cursor: default;
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

.origin-system-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(36, 41, 47));
  border-radius: 3%;
  color: #ffffff;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
}
.origin-user-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  background: linear-gradient(rgb(24, 144, 255));
  border-radius: 3%;
  color: #ffffff;
  font-weight: 500;
  text-align: center;
  padding: 1px 3px;
  min-width: 60px;
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

.plugin-num-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  border-radius: 3%;
  padding: 1px 4.5px;
  background: linear-gradient(rgb(253, 135, 1));
  color: #ffffff;
  font-weight: 500;
  text-align: center;
}

.flow-ref-num-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  border-radius: 3%;
  padding: 1px 4.5px;
  background: linear-gradient(#1890ff);
  color: #ffffff;
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

.steps-action {
  margin-top: 24px;
  text-align: right;
}

.spin-content {
  border: 1px solid #91d5ff;
  background-color: #e6f7ff;
  padding: 30px;
}

/deep/ .ant-descriptions-item-label {
  font-weight: 500
}

/deep/ .plugin-view-card {
    .ant-card-head {
        padding-top: 5px;
        .ant-card-head-wrapper {
            margin-top: -30px;
            .ant-card-head-title {
              font-weight: 700;
              font-size: 22px;
              color: rgb(0, 128, 233);
              -webkit-font-smoothing: antialiased
            }
        }
    }
}

/deep/ .steps-content {
    min-height: calc(55vh);
    .ant-card-body {
        .ant-space{
            .ant-space-item{
                .ant-row{
                    .ant-card{
                        .ant-card-body{
                            padding-bottom: 0px;
                            padding-right: 0px
                        }
                        .ant-row {
                            .ant-result{
                                padding: 0px;
                                padding-bottom: 8px;
                                padding-top: 12px;
                            }
                        }
                    }
                }
            }
        }
    }
}

/deep/ .ant-result {
    padding: 0px;
}

/deep/ .ant-steps-item {
    .ant-steps-item-container {
        .ant-steps-item-content {
            .ant-steps-item-description {
                max-width: 100%;
            }
        }
    }
}
.check-button {
    width: 100px;
}

</style>
