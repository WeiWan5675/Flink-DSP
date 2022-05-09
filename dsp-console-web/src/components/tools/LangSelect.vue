<template>
  <a-dropdown>
    <span class="action global-lang">
      <a-icon type="global" style="font-size: 16px"/>
    </span>
    <a-menu slot="overlay" style="width: 150px;" @click="SwitchLang" :selectedKeys="selectedLocale">
      <a-menu-item key="zh">
        <a rel="noopener noreferrer">
          <span role="img" aria-label="简体中文">CN</span> 简体中文
        </a>
      </a-menu-item>
      <a-menu-item key="en">
        <a rel="noopener noreferrer">
          <span role="img" aria-label="English">EN</span> English
        </a>
      </a-menu-item>
    </a-menu>
  </a-dropdown>
</template>

<script>
// import { mixin as langMixin } from '@/store/i18n-mixin'

export default {
  name: 'LangSelect',
  // mixins: [langMixin],
  data () {
    return {
      selectedLocale: ['en']
    }
  },
  methods: {
    SwitchLang (row) {
      this.$i18n.locale = row.key
      this.selectedLocale = [row.key]
    }
  }
}
</script>
