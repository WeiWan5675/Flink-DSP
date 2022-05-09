#!/bin/bash
# shellcheck disable=SC2068
source "$(
  cd "$(dirname "$0")"/..
  pwd
)"/bin/dsp-env.sh

function run() {
  $JAVA_RUN $DSP_CLIENT_JAVA_OPTS $DSP_CUSTOM_OPTS -classpath $DSP_CLASS_PATH com.weiwan.dsp.client.DspCli run ${@:1}
}

function stop() {
  $JAVA_RUN $DSP_CLIENT_JAVA_OPTS $DSP_CUSTOM_OPTS -classpath $DSP_CLASS_PATH com.weiwan.dsp.client.DspCli stop ${@:1}
}

function info() {
  $JAVA_RUN $DSP_CLIENT_JAVA_OPTS $DSP_CUSTOM_OPTS -classpath $DSP_CLASS_PATH com.weiwan.dsp.client.DspCli info ${@:1}
}

function list() {
  $JAVA_RUN $DSP_CLIENT_JAVA_OPTS $DSP_CUSTOM_OPTS -classpath $DSP_CLASS_PATH com.weiwan.dsp.client.DspCli list ${@:1}
}

case $1 in
'run')
  run ${@:1}
  ;;
'stop')
  stop ${@:1}
  ;;
'info')
  info ${@:1}
  ;;
'list')
  list ${@:1}
  ;;
*)
  echo "未知"
  exit 1
  ;;
esac
