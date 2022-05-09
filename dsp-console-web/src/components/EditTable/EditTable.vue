<template>
  <div>
    <a-card :bordered="true" :title="title" size="small">
      <div slot="extra">
        <a-col>
          <a-button type="primary" size="small" @click="handleAdd">
            {{ $t('form.add') }}
          </a-button>
        </a-col>
      </div>
      <a-table
        v-if="dataSource.length > 0"
        bordered
        row-key="index"
        :data-source="dataSource"
        :columns="table_columns"
        :pagination="false"
        size="small">
        <template slot="edit-cell-key" slot-scope="text, record">
          <editable-cell :text="record.key" @change="onCellChange(record, 'key', $event)" />
        </template>
        <template slot="edit-cell-value" slot-scope="text, record">
          <editable-cell :text="record.value" @change="onCellChange(record, 'value', $event)" />
        </template>
        <template slot="edit-cell-desc" slot-scope="text, record">
          <editable-cell :text="record.description" @change="onCellChange(record, 'desc', $event)" />
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-popconfirm
            v-if="dataSource.length"
            :title="$t('form.deleteCheck')"
            :ok-text="$t('form.deleteOk')"
            :cancel-text="$t('form.deleteCancel')"
            @confirm="() => onDelete(record)"
          >
            <a href="javascript:;">{{ $t('form.delete') }}</a>
          </a-popconfirm>
        </template>
      </a-table>
    </a-card>
  </div>
</template>
<script>
const EditableCell = {
  template: `
      <div class="editable-cell">
        <div v-if="editable" class="editable-cell-input-wrapper">
          <a-input :value="value" @change="handleChange" @pressEnter="check" /><a-icon
            type="check"
            class="editable-cell-icon-check"
            @click="check"
          />
        </div>
        <div v-else class="editable-cell-text-wrapper">
          {{ value || ' ' }}
          <a-icon type="edit" class="editable-cell-icon" @click="edit" />
        </div>
      </div>
    `,
  props: {
    text: String
  },
  data () {
    return {
      value: this.text,
      editable: false
    }
  },
  methods: {
    handleChange (e) {
      const value = e.target.value
      this.value = value
    },
    check () {
      this.editable = false
      this.$emit('change', this.value)
    },
    edit () {
      this.editable = true
    }
  }
}
export default {
  components: {
    EditableCell
  },
  props: {
    value: {
      type: Array,
      default: undefined
    },
    title: {
      type: String,
      default: 'Vars'
    }
  },
  data () {
    return {
      configs: [],
      configsMap: {},
      dataSource: [
      ]
    }
  },
  computed: {
    table_columns () {
      const columns = [
        {
          title: this.$t('form.key'),
          dataIndex: 'key',
          width: '30%',
          scopedSlots: { customRender: 'edit-cell-key' }
        },
        {
          title: this.$t('form.value'),
          dataIndex: 'value',
          width: '30%',
          scopedSlots: { customRender: 'edit-cell-value' }
        },
        {
          title: this.$t('form.desc'),
          dataIndex: 'description',
          width: '30%',
          scopedSlots: { customRender: 'edit-cell-desc' }
        },
        {
          title: this.$t('form.action'),
          dataIndex: 'operation',
          width: '10%',
          scopedSlots: { customRender: 'operation' }
        }
      ]
      return columns
    }
  },
  watch: {
    value: {
      immediate: true, // immediate选项可以开启首次赋值监听
      async handler (newData, oldData) {
        newData.forEach(item => {
          console.log('key: ', item.key)
          console.log('value: ', item.value)
          this.dataSource.push({
            index: item.key,
            key: item.key,
            value: item.value,
            description: item.description
          })
        })
      }
    }
  },
  methods: {
    onCellChange (record, name, text) {
      console.log('onCellChange-record', record)
      console.log('onCellChange-name', name)
      console.log('onCellChange-text', text)
      if (name === 'key') {
        record.key = text
      } else if (name === 'value') {
        record.value = text
      } else if (name === 'desc') {
        record.description = text
      }
      console.log('datasource', this.dataSource)
      this.$emit('change', this.dataSource)
    },
    onDelete (record) {
      console.log('record', record)
      const dataSource = [...this.dataSource]
      this.dataSource = dataSource.filter(item => item.index !== record.index)
      this.$emit('change', this.dataSource)
    },
    handleAdd () {
      const { dataSource } = this
      var timestamp = new Date().getTime()
      const newData = {
        index: timestamp,
        key: '',
        value: '',
        description: ''
      }
      this.dataSource = [...dataSource, newData]
    }
  }
}
</script>
<style lang="less">
.editable-cell {
  position: relative;
}

.editable-cell-input-wrapper,
.editable-cell-text-wrapper {
  padding-right: 24px;
  max-height: 28px;
}

.editable-cell-text-wrapper {
  padding: 5px 24px 5px 5px;
}

.editable-cell-icon,
.editable-cell-icon-check {
  position: absolute;
  right: 0;
  width: 20px;
  cursor: pointer;
}

.editable-cell-icon {
  line-height: 18px;
  display: none;
}

.editable-cell-icon-check {
  line-height: 28px;
}

.editable-cell:hover .editable-cell-icon {
  display: inline-block;
}

.editable-cell-icon:hover,
.editable-cell-icon-check:hover {
  color: #108ee9;
}

.editable-add-btn {
  margin-bottom: 8px;
}

/deep/ .ant-card {
   margin-top: 0px;
   padding: 5px;
  .ant-card-head {
    .ant-card-head-wrapper {
      padding: 5px 5px 5px 5px;
      .ant-card-head-title {
        padding: 0px 0px 0px 0px;
      }
      .ant-card-extra {
        padding: 0px 0px 0px 0px;
      }
    }
  }
}

/deep/ .ant-form-item-control {
    .ant-form-item-children {
        .ant-card .ant-card-bordered .ant-card-small {
            .ant-card-body {
                padding: 0px 0px 0px 0px;
            }
        }
    }
}
</style>
