<template>
  <div class="overflow-y: scroll;">
    <a-list size="small" :data-source="configs" :split="false">
      <a-list-item slot="renderItem" slot-scope="config">
        {{ config.key }} &nbsp;&nbsp;
        <a-tooltip :title="config.description">
          <a-icon type="question-circle-o" />
        </a-tooltip>
        <a-row v-if="'text' === getInputType(config)" >
          <a-input :type="getTextType(config)" :default-value="getDefaultValue(config, 'text')" @blur="handleTextInputChange(config, $event)"></a-input>
        </a-row>
        <a-row v-else-if="'text-password' === getInputType(config)" >
          <a-input type="password" :default-value="getDefaultValue(config, 'text')" @blur="handleTextInputChange(config, $event)"></a-input>
        </a-row>
        <a-row v-else-if="'text-textarea' === getInputType(config)" >
          <a-input type="textarea" :default-value="getDefaultValue(config, 'text')" :rows="5" @blur="handleTextInputChange(config, $event)"></a-input>
        </a-row>
        <a-row v-else-if="'num' === getInputType(config)" >
          <a-input-number style="width: 100%" :default-value="getDefaultValue(config, 'num')" @blur="handleNumInputChange(config, $event)"></a-input-number >
        </a-row>
        <a-row v-else-if="'switch' === getInputType(config)" >
          <a-switch :v-model="getDefaultValue(config, 'switch')" @change="handleSwitchChange(config, $event)"></a-switch>
        </a-row>
        <a-row v-else-if="'select-object' === getInputType(config)">
          <a-select
            label-in-value
            :default-value="{key: getDefaultValue(config, 'select-object')}"
            @change="handleObjectSelectChange(config, $event)"
          >
            <a-select-option :key="option.code" v-for="option in config.optionals">
              {{ option.type }}
            </a-select-option>
          </a-select>
        </a-row>
        <a-row v-else-if="'select-text' === getInputType(config)">
          <a-select
            label-in-value
            :default-value="{key: getDefaultValue(config, 'select-text')}"
            @change="handleTextSelectChange(config, $event)"
          >
            <a-select-option :key="option" v-for="option in config.optionals">
              {{ option }}
            </a-select-option>
          </a-select>
        </a-row>
      </a-list-item>
    </a-list>
  </div>
</template>
<script>
const getOptionType = function (type) {
  if (type === null || type === undefined) return String
  const t = type.toLowerCase()
  switch (t) {
    case 'java.lang.string':
    case 'string':
    case 'java.lang.char':
    case 'varchar':
    case 'str':
      return String
    case 'java.lang.integer':
    case 'int':
    case 'integer':
    case 'number':
    case 'long':
    case 'java.lang.long':
    case 'java.lang.double':
    case 'java.lang.float':
    case 'java.lang.byte':
    case 'byte':
    case 'double':
    case 'float':
      return Number
    case 'java.lang.boolean':
    case 'boolean':
      return Boolean
    case 'com.weiwan.dsp.api.enums.enginetype':
    case 'com.weiwan.dsp.api.enums.flinkcheckpointmode':
    case 'com.weiwan.dsp.api.enums.resolveorder':
    case 'com.weiwan.dsp.api.enums.enginemode':
    case 'java.util.map':
      return Object
    case 'java.util.list':
    case 'array[]':
    case 'array':
      return Array
    default:
      return String
  }
}
export default {
  components: {
  },
  props: {
    value: {
      type: Object,
      default: undefined
    },
    styles: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      configs: [],
      configsMap: {}
    }
  },
  computed: {
  },
  watch: {
    value: {
      immediate: true, // immediate选项可以开启首次赋值监听
      async handler (newData, oldData) {
        console.log('newData-value:', newData)
        this.configs = Object.values(newData)
        this.handleChange()
        console.log('this.configs', this.configs)
      }
    }
  },
  methods: {
    async handleChange () {
      this.$emit('change', this.configsMap)
    },
    handleNumInputChange (config, event) {
      console.log('eeeeeeeeeeeee', event.target.value)
      let obj = this.configsMap[config.key]
      if (!obj) {
        obj = Object.assign({}, config)
        this.configsMap[config.key] = obj
      } else {
        obj = this.configsMap[config.key]
      }
      obj.value = Number(event.target.value)
      console.log('ccccccccccccc', obj)
      console.log('configsMaps', this.configsMap)
      this.handleChange()
    },
    handleTextInputChange (config, event) {
      console.log('eeeeeeeeeeeee', event.target.value)
      let obj = this.configsMap[config.key]
      if (!obj) {
        obj = Object.assign({}, config)
        this.configsMap[config.key] = obj
      } else {
        obj = this.configsMap[config.key]
      }
      obj.value = String(event.target.value)
      console.log('ccccccccccccc', obj)
      console.log('configsMaps', this.configsMap)
      this.handleChange()
    },
    handleTextSelectChange (config, event) {
      console.log('eeeeeeeeeeeee', event)
      let obj = this.configsMap[config.key]
      if (!obj) {
        obj = Object.assign({}, config)
        this.configsMap[config.key] = obj
      } else {
        obj = this.configsMap[config.key]
      }
      obj.value = String(event.key)
      console.log('ccccccccccccc', obj)
      console.log('configsMaps', this.configsMap)
      this.handleChange()
    },
    handleObjectSelectChange (config, event) {
      console.log('eeeeeeeeeeeee', event)
      let obj = this.configsMap[config.key]
      if (!obj) {
        obj = Object.assign({}, config)
        this.configsMap[config.key] = obj
      } else {
        obj = this.configsMap[config.key]
      }
      obj.value = { type: event.label, code: event.key }
      console.log('ccccccccccccc', obj)
      console.log('configsMaps', this.configsMap)
      this.handleChange()
    },
    handleSwitchChange (config, event) {
      console.log('eeeeeeeeeeeee', event)
      let obj = this.configsMap[config.key]
      if (!obj) {
        obj = Object.assign({}, config)
        this.configsMap[config.key] = obj
      } else {
        obj = this.configsMap[config.key]
      }
      obj.value = Boolean(event)
      console.log('ccccccccccccc', obj)
      console.log('configsMaps', this.configsMap)
      this.handleChange()
    },
    getInputType (config) {
      const type = config.type
      const _type = getOptionType(type)
      switch (_type) {
        case String:
          if (config.optionals) {
            return 'select-text'
          }
          if (config.password) {
            return 'text-password'
          }
          if (config.textarea) {
            return 'text-textarea'
          }
          return 'text'
        case Number:
          return 'num'
        case Boolean:
          return 'switch'
        case Object:
          return 'select-object'
        default:
          return 'text'
      }
    },
    getTextType (config) {
      const pass = config.password
      const textarea = config.textarea
      if (pass) {
        return 'password'
      }
      if (textarea) {
        return 'textarea'
      }
      return 'text'
    },
    getDefaultValue (config, inputType) {
      switch (inputType) {
        case 'text':
          if (config.value === undefined || config.value === null || config.value === '') {
            return config.defaultValue
          } else {
            return config.value
          }
        case 'num':
          if (config.value === undefined || config.value === null || config.value === '') {
            return config.defaultValue
          } else {
            return config.value
          }
        case 'select-text':
          if (config.value === undefined || config.value === null || config.value === '') {
            console.log('select-tex-config.defaultValue', config.defaultValue)
            return config.defaultValue
          } else {
            console.log('select-tex-els-config.value', config.value)
            return config.value
          }
        case 'select-object':
          if (config.value === undefined || config.value === null || config.value === '') {
            console.log('select-obj-config.defaultValue', config.defaultValue)
            return config.defaultValue.code
          } else {
            console.log('select-obj-els-config.defaultValue', config.value)
            return config.value.code
          }
        case 'switch':
          if (config.value === undefined || config.value === null || config.value === '') {
            return config.defaultValue
          } else {
            return config.value
          }
        default:
          if (config.value === undefined || config.value === null || config.value === '') {
            return config.defaultValue
          } else {
            return config.value
          }
      }
    }
  }
}
</script>
<style lang="less" scoped>
/deep/ .ant-collapse-item {
.ant-collapse-header {
    padding-bottom: 24px;
}
}
</style>
