#!/bin/bash

# shellcheck disable=SC2046
# shellcheck disable=SC2164
# shellcheck disable=SC2231
####################################################
##    作者:肖振男
##    功能:Flink-DSP程序环境相关设置
##    修改日期:
##    备注:
####################################################
source ${HOME}/.bash_profile
source /etc/profile
JAVA_HOME=${JAVA_HOME}

OS="Unknown"
CPSP=":"
SEPARATOR="/"
if [ "$(uname)" == "Darwin" ]; then
  OS="Mac OS X"
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
  OS="Linux"
elif [ "$(expr substr $(uname -s) 9 2)" == "NT" ]; then
  CPSP=";"
  SEPARATOR="\\"
  OS="Windows"
fi

#echo "当前运行环境: "$OS

DSP_HOME=${DSP_HOME}
if [ -z "${DSP_HOME}" ]; then
  DSP_HOME="$(

    cd "$(dirname "$0")"${SEPARATOR}..
    pwd
  )"
fi

#声明变量
DSP_HOME=${DSP_HOME}
DSP_CONF_DIR=$DSP_HOME${SEPARATOR}conf
DSP_LIB_DIR=$DSP_HOME${SEPARATOR}lib
DSP_BIN_DIR=$DSP_HOME${SEPARATOR}bin
DSP_EXTLIB_DIR=$DSP_HOME${SEPARATOR}ext-lib
DSP_PLUGINS_DIR=$DSP_HOME${SEPARATOR}plugin
DSP_LOG_DIR=$DSP_HOME${SEPARATOR}logs
DSP_TMP_DIR=$DSP_HOME${SEPARATOR}tmp

#创建临时文件夹文件夹
if [ -z "$DSP_HOME" ]; then
  mkdir -p $DSP_LOG_DIR
  mkdir -p $DSP_TMP_DIR
fi

# Find the java binary
if [ -n "${JAVA_HOME}" ]; then
  JAVA_RUN="${JAVA_HOME}${SEPARATOR}bin${SEPARATOR}java"
else
  if [ $(command -v java) ]; then
    JAVA_RUN="java"
  else
    echo "JAVA_HOME is not set" >&2
    exit 1
  fi
fi

#java class path
CLASS_PATH=".$CPSP$JAVA_HOME${SEPARATOR}lib$CPSP$JAVA_HOME${SEPARATOR}lib${SEPARATOR}dt.jar$CPSP$JAVA_HOME${SEPARATOR}lib${SEPARATOR}tools.jar"
for jar in $(find $DSP_LIB_DIR -name *.jar); do
  CLASS_PATH="$CLASS_PATH$CPSP$jar"
done
for jar in $(find $DSP_PLUGINS_DIR -name *.jar); do
  CLASS_PATH="$CLASS_PATH$CPSP$jar"
done
for jar in $(find $DSP_EXTLIB_DIR -name *.jar); do
  CLASS_PATH="$CLASS_PATH$CPSP$jar"
done

#echo "$JAVA_HOME"
#echo "$JAVA_RUN"
#声明变量
export SEPARATOR=$SEPARATOR
export CPSP=$CPSP
export OS=%OS
export DSP_HOME=${DSP_HOME}
export DSP_CONF_DIR=${DSP_CONF_DIR}
export DSP_LIB_DIR=${DSP_LIB_DIR}
export DSP_BIN_DIR=${DSP_BIN_DIR}
export DSP_EXTLIB_DIR=${DSP_EXTLIB_DIR}
export DSP_PLUGINS_DIR=${DSP_PLUGINS_DIR}
export DSP_LOG_DIR=${DSP_LOG_DIR}
export DSP_TMP_DIR=${DSP_TMP_DIR}
export DSP_CLASS_PATH=$CLASS_PATH
export DSP_JAVA_RUN=${JAVA_RUN}

export DSP_CURRENT_USER=${USER}
export DSP_CONSOLE_JAVA_OPTS="-Xms512m -Xmx1024m -Djava.awt.headless=true"
export DSP_CLIENT_JAVA_OPTS="-Xms512m -Xmx1024m -Djava.awt.headless=true -Ddsp.client.log.path=${DSP_LOG_DIR} -Dlog4j.configurationFile=${DSP_CONF_DIR}/log4j-cli.properties"
export DSP_CUSTOM_OPTS="-Ddsp.base.dir=${DSP_HOME} -Ddsp.conf.dir=${DSP_CONF_DIR}"


#Info日志输出
function logger_info (){
	echo -e `date +%Y-%m-%d\ %H:%M:%S` : "INFO [${0##*/}] : ${1}"
	return $?

}

#错误日志输出
function logger_err (){
	echo -e `date +%Y-%m-%d\ %H:%M:%S` : "ERROR [${0##*/}] : ${1}"
	return $?
}

#加载properties配置文件
function load_conf() {
    value=`cat $1 | grep $2 | awk -F'=' '{ print $2 }'`
    echo $value
}


#解析json方法,使用的是服务器上带的python2.7
#有两个入参 1. 解析的文件 2. 表达式 "['province'][1]['name']"
function load_json(){
  file=$1
  expres="${2}"
  cat ${file} | /usr/bin/python2.7 -c "import json; import sys; obj=json.load(sys.stdin); print obj"${expres}".encode('utf-8')"
  exit $?
}