<template>
  <div class="user-wrapper">
    <div class="content-box">
      <a-modal
        :visible="showVersion"
        @ok="handleVersionChange"
        @cancel="handleVersionChange"
        :footer="null"
      >
        <a-descriptions :title="this.$t('userManage.versionTitle')" :column="2">
          <a-descriptions-item :label="$t('dsp.version')">
            {{ versionInfo.version }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('dsp.buildVersion')">
            {{ versionInfo.buildVersion }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('dsp.jdkVersion')">
            {{ versionInfo.jdkVersion }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('dsp.mavenVersion')">
            {{ versionInfo.mavenVersion }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('dsp.flinkVersion')">
            {{ versionInfo.flinkVersion }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('dsp.hadoopVersion')">
            {{ versionInfo.hadoopVersion }}
          </a-descriptions-item>
        </a-descriptions>
      </a-modal>
      <a href="dsp/docs" class="nav-link" style="color: #242424;margin-right: 12px;margin-left: 12px;font-size: 14px;font-weight: 600;">
        {{ $t('userManage.docs') }}
        <svg
          viewBox="0 0 100 100"
          width="16"
          height="16"
          style="margin-bottom: -3px">
          <path fill="currentColor" d="M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z"></path>
          <polygon fill="currentColor" points="45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9"></polygon>
        </svg>
      </a>
      <a-divider type="vertical" style="margin: 0px 0px 0px 0px;"/>
      <a href="https://github.com/WeiWan5675/Flink-DSP" style="color: #242424">
        <a-icon type="github" style="margin-right: 12px;margin-left: 12px;width: 16px;height: 16px"/>
      </a>
      <a-divider type="vertical" style="margin: 0px 0px 0px 0px;"/>
      <LangSelect/>
      <a-divider type="vertical" style="margin: 0px 0px 0px 0px;"/>
      <a-dropdown>
        <span class="action ant-dropdown-link user-dropdown-menu">
          <a-icon type="user"/>
          <span style="color: #242424; margin-left: 5px;margin-bottom: 5px;font-size: 16px;font-weight: 600;">{{ nickname }}</span>
        </span>
        <a-menu slot="overlay" class="user-dropdown-menu-wrapper" >
          <a-menu-item key="0">
            <a-icon type="user"/>
            <span style="color: #242424; margin-left: 5px;margin-bottom: 5px;font-size: 14px;font-weight: 500;">{{ nickname }}</span>
          </a-menu-item>
          <a-menu-item key="1">
            <a href="javascript:;" @click="openAbout">
              <a-icon type="question-circle" />
              <span style="color: #242424; margin-left: 5px;margin-bottom: 5px;font-size: 14px;font-weight: 500;">{{ $t('userManage.version') }}</span>
            </a>
          </a-menu-item>
          <a-menu-divider/>
          <a-menu-item key="2">
            <a href="javascript:;" @click="handleLogout">
              <a-icon type="logout"/>
              <span style="color: #242424; margin-left: 5px;margin-bottom: 5px;font-size: 14px;font-weight: 500;">{{ $t('userManage.logout') }}</span>
            </a>
          </a-menu-item>
        </a-menu>
      </a-dropdown>
    </div>
  </div>
</template>

<script>
import NoticeIcon from '@/components/NoticeIcon'
import { mapActions, mapGetters } from 'vuex'
import LangSelect from '@/components/tools/LangSelect'
export default {
  name: 'UserMenu',
  components: {
    NoticeIcon,
    LangSelect
  },
  computed: {
    ...mapGetters(['nickname', 'avatar'])

  },
  data () {
    return {
      showVersion: false,
      versionInfo: {}
    }
  },
  methods: {
    changeZh () {
      this.$i18n.locale = 'zh'
    },
    changeEn () {
      this.$i18n.locale = 'en'
    },
    ...mapActions(['Logout', 'GetVersion']),
    handleLogout () {
      this.$confirm({
        title: this.$t('message.warn'),
        content: this.$t('message.logout'),
        onOk: () => {
          return this.Logout({}).then(() => {
            setTimeout(() => {
              window.location.reload()
            }, 16)
          }).catch(err => {
            this.$message.error({
              title: '错误',
              description: err.message
            })
          })
        },
        onCancel () {
        }
      })
    },
    openAbout () {
      this.GetVersion().then(r => {
        if (r.success) {
          this.versionInfo = r.result
          console.log('version info', r.result)
        } else {
        }
      })
      this.showVersion = true
    },
    handleVersionChange () {
      this.showVersion = false
    }
  }
}
</script>
