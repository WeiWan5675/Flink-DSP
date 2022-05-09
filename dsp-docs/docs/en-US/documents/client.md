---
title: 使用客户端
autoSort: 997
---

## 创建一个作业

可以通过手工创建job-flow.json,描述一个作业的配置,具体的配置模板


## 提交一个作业

创建好一个作业的描述文件后,可以通过以下命令进行作业的提交
$DPS_HOME/dsp.sh run -c conf/example/job-flow.json

## 提交作业到yarn

- 修改默认配置文件

修改conf/dsp-core.yaml 将dsp.core.engineConfig.engineType 修改为Flink

修改conf/dsp-core.yaml 将dsp.core.engineConfig.engineMode 修改为yarn-pre-job


- 在作业描述文件中设置

同上

## 作业停止

$DSP_HOME/dsp.sh stop -jobId 123124121514

## 作业查看

$DSP_HOME/dsp.sh info -jobId 123124121514


## 参数说明


暂无