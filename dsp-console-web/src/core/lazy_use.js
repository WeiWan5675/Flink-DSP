import Vue from 'vue'
import VueStorage from 'vue-ls'
import config from '@/config/defaultSettings'

// base library
import '@/core/lazy_lib/components_use'
import Viser from 'viser-vue'

// ext library
import VueClipboard from 'vue-clipboard2'
import MultiTab from '@/components/MultiTab'
import PageLoading from '@/components/PageLoading'
import PermissionHelper from '@/utils/helper/permission'
import './directives/action'
import { Collapse } from 'ant-design-vue'
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue'
import hljs from 'highlight.js'
import VueHighlightJS from 'vue-highlightjs'
import('highlight.js/styles/idea.css')
Vue.directive('highlight', function (el) {
  const blocks = el.querySelectorAll('pre code')
  blocks.forEach(block => {
    hljs.highlightBlock(block)
  })
})
// 增加组定义属性，用于在代码中预处理代码格式
Vue.prototype.$hljs = hljs
Vue.use(hljs)
Vue.use(VueHighlightJS)
VueClipboard.config.autoSetContainer = true
Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)
Vue.use(Collapse)
Vue.use(Collapse)
Vue.use(Viser)
Vue.use(MultiTab)
Vue.use(PageLoading)
Vue.use(VueStorage, config.storageOptions)
Vue.use(VueClipboard)
Vue.use(PermissionHelper)
