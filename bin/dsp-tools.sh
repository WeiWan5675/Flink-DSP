#!/bin/bash

source "$(
  cd "$(dirname "$0")"/..
  pwd
)"/bin/dsp-env.sh




install() {
  echo "install dsp to local"
}

uninstall() {
  echo "uninstall dsp from local"
}

get_version() {
  local CLASS_PATH=$CLASS_PATH
  $DSP_JAVA_RUN $DSP_CUSTOM_OPTS -classpath "$DSP_CLASS_PATH" com.weiwan.dsp.console.DspSystemApp -v
}

print_help() {
  local CLASS_PATH=$CLASS_PATH
  $DSP_JAVA_RUN $DSP_CUSTOM_OPTS -classpath "$DSP_CLASS_PATH" com.weiwan.dsp.client.DspCli run -h
}

case "$1" in
'install')
  install
  ;;
'uninstall')
  uninstall
  ;;
'version')
  get_version
  ;;
'help')
  print_help
  ;;
*)
  exit 1
  ;;
esac
