<template>
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
</template>
<script>

export default ({
  props: {
    text: {
      type: String,
      default: ''
    }
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
})
</script>
<style>
.editable-cell {
  position: relative;
  max-width: 500px;
}

.editable-cell-input-wrapper,
.editable-cell-text-wrapper {
  padding-right: 24px;
  max-width: 500px;
  min-height: 32px;
  overflow:hidden;
  text-overflow: ellipsis;
  white-space:nowrap;
  width: 100%;
}

.editable-cell-text-wrapper {
  padding: 5px 24px 15px 5px;
}

.editable-cell-icon,
.editable-cell-icon-check {
  position: absolute;
  right: 0;
  width: 20px;
  margin-top: 5px;
  cursor: pointer;
}

.editable-cell-icon {
  line-height: 32px;
  display: none;
}

.editable-cell-icon-check {
  line-height: 32px;
}

.editable-cell .editable-cell-icon {
  display: inline-block;
}

.editable-cell-icon,
.editable-cell-icon-check {
  color: #000000;
}

.editable-add-btn {
  margin-bottom: 8px;
}
</style>
