---
title: 快速开始
autoSort: 998
---


## Console使用
暂无，后边编写


## 命令行使用
- 创建一个Job配置

创建的job配置文件的模板如下
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
          "pluginConfig": {
          }
        },
        "S1": {
          "nodeId": "S1",
          "nodeName": "拆分节点1",
          "nodeType": "split",
          "nodeDependents": [
            "P1"
          ],
          "pluginName": "dsp-common-stream-split",
          "pluginConfig": {
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
        },
        "P1": {
          "nodeId": "P1",
          "nodeName": "数据处理1",
          "nodeType": "Process",
          "nodeDependents": [
            "R1"
          ],
          "pluginName": "dsp-plugin-common-process",
          "pluginConfig": {
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
          "pluginName": "dsp-plugin-common-union",
          "pluginConfig": ""
        },
        "W1": {
          "nodeId": "W1",
          "nodeName": "输出节点1",
          "nodeType": "writer",
          "nodeDependents": [
            "U1"
          ],
          "pluginName": "dsp-plugin-common-writer",
          "pluginConfig": {
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

## 自定义开发

暂无