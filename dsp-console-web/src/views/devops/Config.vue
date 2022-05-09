<template>
  <a-card :bordered="true" title="Total Configs">
AAAAAAAAAAAAA
  </a-card>
</template>

<script>
</script>
<style lang="less" scoped>
  #flow_head_col {
    font-weight: bold;
    font-size: 16px;
    margin-left: 0px;
    padding: auto;
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
.using-mark-tag {
  animation: disable-tag-color 800ms ease-out infinite alternate;
  border-radius: 3%;
  padding: 1px 4.5px;
  background: linear-gradient(rgb(253, 135, 1));
  color: #ffffff;
  font-weight: 500;
  text-align: center;
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
</style>
