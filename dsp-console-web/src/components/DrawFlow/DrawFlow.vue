<script>
export default {
  props: ['level', 'title'],
  // heading组件
  // <heading :level="1" :title="title" icon="cart">{{title}}</heading>
  // <h2 title=""></h2>
  render (h) {
    console.log('this.$slots.default :>> ', this.$slots.default)
    return h(
      'h' + this.level, // 参数1，tagname
      { attrs: { title: this.title } }, // 参数2：{。。。}
      this.$slots.default // 参数3，子节点VNode数组
    )
  }
}
</script>

<style lang="scss" scoped>
</style>
