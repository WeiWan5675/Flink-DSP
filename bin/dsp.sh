#!/bin/bash
# shellcheck disable=SC2068
# shellcheck disable=SC2164
source "$(
  cd "$(dirname "$0")"/..
  pwd
)"/bin/dsp-env.sh

#1. 启动console
#2. 初始化环境
#3. 停止console
#4. 任务提交
#5. 任务停止
#6. 其它

function dsp_console_start() {
  $DSP_BIN_DIR${SEPARATOR}dsp-console.sh start
}

function dsp_console_stop() {
  $DSP_BIN_DIR${SEPARATOR}dsp-console.sh stop
}

function dsp_console_status() {
  $DSP_BIN_DIR${SEPARATOR}dsp-console.sh status
}

function dsp_console_info() {
  $DSP_BIN_DIR${SEPARATOR}dsp-console.sh info
}

function dsp_install() {
  $DSP_BIN_DIR${SEPARATOR}dsp-tools.sh install
}

function dsp_uninstall() {
  $DSP_BIN_DIR${SEPARATOR}dsp-tools.sh uninstall
}

function dsp_version() {
  $DSP_BIN_DIR${SEPARATOR}dsp-tools.sh version
}

function dsp_help() {
    $DSP_BIN_DIR${SEPARATOR}dsp-tools.sh help
}

function dsp_run() {
  $DSP_BIN_DIR/dsp-run.sh ${@:1}
}

case "$1" in
'console')
  case "$2" in
  'start')
    dsp_console_start ${@:3}
    ;;
  'stop')
    dsp_console_stop ${@:3}
    ;;
  'status')
    dsp_console_status ${@:3}
    ;;
  'restart')
    dsp_console_stop ${@:3}
    dsp_console_start ${@:3}
    ;;
  'info')
    dsp_console_info ${@:3}
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status|info}"
    exit 1
    ;;
  esac
  ;;
'install')
  dsp_install ${@:2}
  ;;
'uninstall')
  dsp_uninstall ${@:2}
  ;;
'version' | '-v' | '-version' | '--version')
  dsp_version
  ;;
  'help' | '-h' | '-help' | '--help')
  dsp_help
  ;;
'run' | 'stop' | 'info' | 'list')
  dsp_run ${@:1}
  ;;
*)
  echo "Usage: $0 {console [start|stop|restart|status|info]|install|uninstall|version|[run|stop|cancel|list]}"
  exit 1
  ;;

esac
