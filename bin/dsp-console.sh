#!/bin/sh
# shellcheck disable=SC2046
# shellcheck disable=SC2039
# shellcheck disable=SC2164
source "$(
  cd "$(dirname "$0")"/..
  pwd
)"/bin/dsp-env.sh

#运行用户
RUNNING_USER=$DSP_CURRENT_USER
#程序目录
DSP_HOME=$DSP_HOME
#需要启动的Java主程序（main方法类）
DSP_CONSOLE_MAIN_CLASS=com.weiwan.dsp.console.DspConsoleApp
#java虚拟机启动参数,在dsp-env.sh中配置
JAVA_OPTS=${DSP_CONSOLE_JAVA_OPTS}

DSP_CONSOLE_NAME="Flink-DSP Console"

###################################
#(函数)判断程序是否已启动
#
#说明：
#使用JDK自带的JPS命令及grep命令组合，准确查找pid
#jps 加 l 参数，表示显示java的完整包路径
#使用awk，分割出pid ($1部分)，及Java程序名称($2部分)
###################################
#初始化psid变量（全局）
psid=0
checkpid() {
  javaps=$($JAVA_HOME/bin/jps -l | grep $DSP_CONSOLE_MAIN_CLASS)

  if [ -n "$javaps" ]; then
    psid=$(echo $javaps | awk '{print $1}')
  else
    psid=0
  fi
}

###################################
#(函数)启动程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示程序已启动
#3. 如果程序没有被启动，则执行启动命令行
#4. 启动命令执行后，再次调用checkpid函数
#5. 如果步骤4的结果能够确认程序的pid,则打印[OK]，否则打印[Failed]
#注意：echo -n 表示打印字符后，不换行
#注意: "nohup 某命令 >/dev/null 2>&1 &" 的用法
###################################
start() {
  checkpid

  if [ $psid -ne 0 ]; then
    echo "================================"
    echo "Warn: $DSP_CONSOLE_NAME already started! (pid=$psid)"
    echo "================================"
  else
    echo -n "Starting $DSP_CONSOLE_MAIN_CLASS ..."
    JAVA_CMD="$DSP_JAVA_RUN $JAVA_OPTS $DSP_CUSTOM_OPTS -classpath $DSP_CLASS_PATH $DSP_CONSOLE_MAIN_CLASS"
    nohup $JAVA_CMD >/dev/null 2>&1 &
    sleep 3
    checkpid
    if [ $psid -ne 0 ]; then
      echo "(pid=$psid) [OK]"
      echo "Start $DSP_CONSOLE_NAME [Succeed]"
    else
      echo "Start $DSP_CONSOLE_NAME [Failed]"
    fi

  fi
}

###################################
#(函数)停止程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则开始执行停止，否则，提示程序未运行
#3. 使用kill -9 pid命令进行强制杀死进程
#4. 执行kill命令行紧接其后，马上查看上一句命令的返回值: $?
#5. 如果步骤4的结果$?等于0,则打印[OK]，否则打印[Failed]
#6. 为了防止java程序被启动多次，这里增加反复检查进程，反复杀死的处理（递归调用stop）。
#注意：echo -n 表示打印字符后，不换行
#注意: 在shell编程中，"$?" 表示上一句命令或者一个函数的返回值
###################################
stop() {
  checkpid
  if [ $psid -ne 0 ]; then
    echo "Stopping $DSP_CONSOLE_NAME (pid=$psid) "
    kill -15 $psid
    sleep 1
    if [ $? -eq 0 ]; then
      echo "Stop $DSP_CONSOLE_NAME [OK]"
    else
      kill -9 $psid
      checkpid
      if [ $psid -eq 0 ]; then
        echo "Stop $DSP_CONSOLE_NAME [OK]"
      else
        echo "Stop $DSP_CONSOLE_NAME [Failed]"
      fi
    fi
    checkpid
    if [ $psid -ne 0 ]; then
      stop
    fi
  else
    echo "================================"
    echo "warn: $DSP_CONSOLE_NAME is not running"
    echo "================================"
  fi
}

###################################
#(函数)检查程序运行状态
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示正在运行并表示出pid
#3. 否则，提示程序未运行
###################################
status() {
  checkpid

  if [ $psid -ne 0 ]; then
    echo "$DSP_CONSOLE_NAME is running! (pid=$psid)"
  else
    echo "$DSP_CONSOLE_NAME is not running"
  fi
}

###################################
#(函数)打印系统环境参数
###################################
info() {
  echo "System Information:"
  echo "****************************"
  echo $(head -n 1 /etc/issue)
  echo $(uname -a)
  echo
  echo "JAVA_HOME=$JAVA_HOME"
  echo $($JAVA_HOME/bin/java -version)
  echo
  echo "DSP_HOME=$DSP_HOME"
  echo "DSP_CONSOLE_MAIN_CLASS=$DSP_CONSOLE_MAIN_CLASS"
  echo "****************************"
}

###################################
#读取脚本的第一个参数($1)，进行判断
#参数取值范围：{start|stop|restart|status|info}
#如参数不在指定范围之内，则打印帮助信息
###################################
case "$1" in
'start')
  start
  ;;
'stop')
  stop
  ;;
'restart')
  stop
  start
  ;;
'status')
  status
  ;;
'info')
  info
  ;;
*)
  echo "Usage: $0 {start|stop|restart|status|info}"
  exit 1
  ;;

esac
