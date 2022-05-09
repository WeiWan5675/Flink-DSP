#!/bin/bash


###########################
# 项目构建脚本
#
###########################
source /etc/profile
source $HOME/.bash_profile

MVN_HOME=$MAVEN_HOME
if [ -z "$MVN_HOME" ]; then
  echo "Cannot find Maven Home, please check environment settings"
  exit 1
fi

echo "start to pull the latest code"
git pull
echo "the latest code has been pulled!"

echo "start building the project"
MVN_CMD="$MVN_HOME/bin/mvn clean install -DSkipTests"
$MVN_CMD
echo "The project has been built and the package is saved in: .target"
exit 0



