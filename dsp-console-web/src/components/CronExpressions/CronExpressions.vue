<template>
  <a-card :bordered="true" :title="title" size="small">
    <div slot="extra">
      <a-popover trigger="click" style="margin-right: 10px">
        <a>Five Runs</a>
        <div slot="content">
          <li v-for="(item, index) in lastFiveResult" :key="index">
            {{ (index + 1)+ '. ' + item }}
          </li>
        </div>

      </a-popover>
      <a-popover trigger="click">
        <a>Help</a>
        <pre slot="content">
    CRON表达式是一个字符串，包含六个由空格分隔的字段，表示一组时间
通常作为执行某个程序的时间表。具体的对应关系如下：
*    *    *    *    *    *
-    -    -    -    -    -
|    |    |    |    |    |
|    |    |    |    |    +----- day of week (1 - 7)
|    |    |    |    +---------- month (1 - 12)
|    |    |    +--------------- day of month (1 - 31)
|    |    +-------------------- hour (0 - 23)
|    +------------------------- min (0 - 59)
+------------------------------ second (0 - 59)
For Example:
    每天18点执行一次：0 0 18 * * ?
    每分钟执行一次：0 * * * * ?
</pre>
      </a-popover>
    </div>
    <a-row :gutter="[8,8]" align="middle">
      <a-row :gutter="[8,0]" align="middle" >
        <a-col :span="3" class="cronColTitle" style="text-align: center;">
          秒
        </a-col>
        <a-col :span="3" style="text-align: center;">
          分
        </a-col>
        <a-col :span="3" style="text-align: center;">
          时
        </a-col>
        <a-col :span="3" style="text-align: center;">
          天
        </a-col>
        <a-col :span="3" style="text-align: center;">
          月
        </a-col>
        <a-col :span="3" style="text-align: center;">
          星期
        </a-col>
        <a-col :span="6" style="text-align: center;">
          表达式
        </a-col>
      </a-row>
    </a-row>
    <div :class="cronClass" >
      <a-row :gutter="[8,1]" align="middle">
        <a-col :span="3" class="cronColInput">
          <a-input v-model="cron.second" @change="changeInput(0, $event)"/>
        </a-col>
        <a-col :span="3">
          <a-input v-model="cron.min" @change="changeInput(1, $event)"/>
        </a-col>
        <a-col :span="3">
          <a-input v-model="cron.hour" @change="changeInput(2, $event)"/>
        </a-col>
        <a-col :span="3">
          <a-input v-model="cron.dayOfMonth" @change="changeInput(3, $event)"/>
        </a-col>
        <a-col :span="3">
          <a-input v-model="cron.month" @change="changeInput(4, $event)"/>
        </a-col>
        <a-col :span="3">
          <a-input v-model="cron.dayOfWeek" @change="changeInput(5, $event)"/>
        </a-col>
        <a-col :span="6">
          <a-input v-model="cronExpr" @blur="handleCronChange(undefined, $event)"/>
        </a-col>
      </a-row>
    </div>
  </a-card>
</template>
<script>
const cronParse = require('cron-parser')
export default {
  components: {
  },
  props: {
    value: {
      type: String,
      default: undefined
    },
    title: {
      type: String,
      default: 'Timer Cron'
    },
    // eslint-disable-next-line vue/require-default-prop
    check: {
      type: Function,
      default: () => {}
    }
  },
  data () {
    return {
      cron: {
        second: '',
        min: '',
        hour: '',
        dayOfMonth: '',
        month: '',
        dayOfWeek: ''
      },
      cronArr: [],
      cronExpr: '',
      lastFiveResult: [],
      cronClass: 'cron-success'
    }
  },
  computed: {
  },
  watch: {
    value: {
      immediate: true, // immediate选项可以开启首次赋值监听
      async handler (newData, oldData) {
        this.handleCronChange(newData)
      }
    }
  },
  methods: {
    async handleChange () {
      this.$emit('change', this.cronExpr)
    },
    checkCronExpression (value) {
      if (value) {
        try {
          const interval = cronParse.parseExpression(value)
          console.log('cronDate:', interval.next().toDate())
          this.lastFiveResult = []
          this.lastFiveResult.push(interval.next().toDate())
          this.lastFiveResult.push(interval.next().toDate())
          this.lastFiveResult.push(interval.next().toDate())
          this.lastFiveResult.push(interval.next().toDate())
          this.lastFiveResult.push(interval.next().toDate())
          this.check(true)
          this.cronClass = 'cron-success'
        } catch (e) {
          // eslint-disable-next-line standard/no-callback-literal
          this.check(false, e.message)
          this.cronClass = 'cron-error'
          console.log('非Cron表达式格式，请检查！' + e.message)
        }
      }
    },
    handleCronChange (data, event) {
      console.log('newData.newData', data)
      console.log('event.event', event)
      if (data === undefined && event !== undefined) {
        console.log('this.cronExpr = event.target.value', event.target.value)
        this.cronExpr = event.target.value
        console.log('this.cronExpr = event.target.value', event.target.value)
      } else {
        this.cronExpr = data
      }
      console.log('this.cronExpr', this.cronExpr)
      const splitArr = this.cronExpr.split(' ')
      console.log('splitArr', splitArr)
      if (splitArr.length < 6) {
        console.log('cron expr is failed')
      }
      this.cronArr[0] = splitArr[0] === undefined ? '' : splitArr[0]
      this.cronArr[1] = splitArr[1] === undefined ? '' : splitArr[1]
      this.cronArr[2] = splitArr[2] === undefined ? '' : splitArr[2]
      this.cronArr[3] = splitArr[3] === undefined ? '' : splitArr[3]
      this.cronArr[4] = splitArr[4] === undefined ? '' : splitArr[4]
      this.cronArr[5] = splitArr[5] === undefined ? '' : splitArr[5]

      this.cron.second = this.cronArr[0]
      this.cron.min = this.cronArr[1]
      this.cron.hour = this.cronArr[2]
      this.cron.dayOfMonth = this.cronArr[3]
      this.cron.month = this.cronArr[4]
      this.cron.dayOfWeek = this.cronArr[5]
      console.log('this.cron', this.cron)
      this.checkCronExpression(this.cronExpr)
      this.handleChange()
    },
    changeInput (index, event) {
      console.log('cron index change, index: ', index)
      console.log('cron index change, data: ', event.target.value)
      this.cronArr[index] = event.target.value
      this.handleCronChange(this.cronArr.join(' '))
    }
  }
}
</script>
<style lang="less">
/deep/.cronColInput {
  padding-left: 30px;
}
/deep/.cronColTitle {
  padding-left: 30px;
  text-align: center;
}
.cron-success {
  padding: 5px;
  margin-top: 5px;
}

.cron-error {
  border: 1px solid;
  padding: 5px;
  margin-top: 5px;
  border-color: #f5222d;
  border-width: 1px !important;
  border-radius: 2px;
  animation: glow 800ms ease-out infinite alternate;
}

@keyframes glow {
    0% {
        border-color: #f5222d;
        box-shadow: 0 0 2px rgba(245, 34, 45,.2), inset 0 0 5px rgba(245, 34, 45,.6), 0 1px 0 #f5222d;
    }
    100% {
        border-color: #f5222d;
        box-shadow: 0 0 6px rgba(245, 34, 45,.2), inset 0 0 10px rgba(245, 34, 45,.6), 0 1px 0 #f5222d;
    }
}
</style>
