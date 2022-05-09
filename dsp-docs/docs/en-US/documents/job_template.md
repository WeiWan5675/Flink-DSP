---
title: 作业模板
autoSort: 995
---
## 介绍

为了将数据ETL过程抽象为一个通用的模式，我们采用模板来规定一个作业的流程，通过用户填充模板中必要的参数，由Flink-DSP进行作业文件的解析和执行。
Flink-DSP在运行时，会将模板和用户提供的作业文件合并，生成最终的作业文件，由流程引擎将作业文件解释为DAG的流程图，通过将流程图映射到Flink具体的算子上，从而达到标准化的处理ETL工作。

## 整体结构
```json
{
  "dsp": {
    "flow": {
      "nodes": {
        "R1": {
          "nodeId": "R1",
          "nodeName": "读取节点1",
          "nodeType": "reader",
          "nodeDependents": [
          ],
          "pluginName": "dsp-mysql-plugin-reader",
          "plugins": {
            "dsp-mysql-plugin-reader": {
              "pluginName": "dsp-mysql-plugin-reader",
              "pluginType": "Reader"
            }
          }
        },
        "S1": {
          "nodeId": "S1",
          "nodeName": "拆分节点1",
          "nodeType": "split",
          "nodeDependents": [
            "P1"
          ],
          "plugins": {
            "dsp-common-stream-split": {
              "pluginName": "dsp-common-stream-split",
              "pluginType": "System",
              "splitNum": 3,
              "splitOuts": [
                {
                  "filterMode": "js",
                  "filterExpr": ""
                },
                {
                  "filterMode": "match",
                  "filterExpr": "a=123"
                },
                {
                  "filterMode": "regexp",
                  "filterExpr": "a=^adada&"
                }
              ]
            }
          }
        },
        "P1": {
          "nodeId": "P1",
          "nodeName": "数据处理1",
          "nodeType": "Process",
          "nodeDependents": [
            "R1"
          ],
          "plugins": {
            "dsp-plugin-common-process1" : {
              "pluginName": "dsp-plugin-common-process1",
              "pluginType": "process"
            },
            "dsp-plugin-common-process2" : {
              "pluginName": "dsp-plugin-common-process2",
              "pluginType": "process"
            }
          }
        },
        "U1": {
          "nodeId": "U1",
          "nodeName": "合并节点1",
          "nodeType": "union",
          "nodeDependents": [
            "S1.0",
            "S1.1",
            "S1.2"
          ],
          "plugins": {
            "dsp-plugin-common-union" : {
              "pluginName": "dsp-plugin-common-union",
              "pluginType": "System"
            }
          }
        },
        "W1": {
          "nodeId": "W1",
          "nodeName": "输出节点1",
          "nodeType": "writer",
          "nodeDependents": [
            "U1"
          ],
          "pluginName": "dsp-plugin-common-writer",
          "plugins": {
            "dsp-plugin-common-writer": {
              "pluginName": "dsp-plugin-common-writer",
              "pluginType": "Writer",
              "writerPath": "/tmp/"
            }
          }
        }
      }
    },
    "core": {
      "engineConfig": {
        "mode": "yarn-pre",
        "mode": "yarn-session",
        "mode": "yarn-application",
        "mode": "standalone"
      },
      "loggingConfig": {
        "level": "info"
      },
      "metricConfig": {
        "reportServer": "127.0.0.1:8989"
      },
      "appConfig": {
        "osName": "linux"
      }
    },
    "custom": {
      "a": "a",
      "b": "b"
    }
  }
}
```

## 核心配置
- engineConfig
  
配置作业运行是任务执行引擎的相关内容,这里的执行引擎指的是底层依赖的具体计算框架。

- appConfig

    系统配置包含几部分
    1. 错误数据处理配置
    2. 限流配置
    3. 通知配置
    4. 其他

- loggingConfig

    主要配置作业日志输出相关

- metricConfig
    1. 定义Metrics指标
    2. Metrics服务地址
    3. MetricsReport初始化参数

## 流程配置

- 节点列表
    由五种节点类型组成的一个Map

- 节点属性
    包含以下内容
  0. 节点ID
  1. 节点名称
    2. 节点类型
    3. 节点依赖关系
    4. 节点插件

- 节点插件
    节点插件属性为一个Map
  插件中属性的定义由具体的插件决定，有两个必要属性分别是 插件名称和插件类型


## 自定义配置

所有需要全局使用的参数，都可以在custom这个进行配置，在所有数据插件中可以通过getCustomVars获取对应的参数。



