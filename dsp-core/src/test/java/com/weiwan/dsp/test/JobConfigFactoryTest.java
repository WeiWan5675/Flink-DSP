//package com.weiwan.dsp.test;
//
//import com.weiwan.dsp.api.config.JobConfig;
//import com.weiwan.dsp.core.utils.DspConfigFactory;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//
///**
// * @author: xiaozhennan
// * @email: xiaozhennan1995@gmail.com
// * @date: 2021/4/14 16:49
// * @description:
// */
//public class JobConfigFactoryTest {
//
//    @Test
//    public void testLoadFunc(){
//        try {
//            JobConfig load1 = DspConfigFactory.load(new File("D:\\develop\\github\\Flink-DSP\\dsp-core\\src\\main\\resources\\job-flow.properties"));
//
//            JobConfig load2 = DspConfigFactory.load(new File("D:\\develop\\github\\Flink-DSP\\dsp-core\\src\\main\\resources\\job-flow.yaml"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testLoadFLow(){
//        try {
//
//            JobConfig load1 = DspConfigFactory.loadFromJson("{\n" +
//                    "  \"dsp\": {\n" +
//                    "    \"flow\": {\n" +
//                    "      \"nodes\": {\n" +
//                    "        \"R1\": {\n" +
//                    "          \"nodeId\": \"R1\",\n" +
//                    "          \"nodeName\": \"读取节点1\",\n" +
//                    "          \"nodeType\": \"reader\",\n" +
//                    "          \"nodeDependents\": [\n" +
//                    "          ],\n" +
//                    "          \"pluginName\": \"dsp-mysql-plugin-reader\",\n" +
//                    "          \"pluginConfig\": {\n" +
//                    "          }\n" +
//                    "        },\n" +
//                    "        \"S1\": {\n" +
//                    "          \"nodeId\": \"S1\",\n" +
//                    "          \"nodeName\": \"拆分节点1\",\n" +
//                    "          \"nodeType\": \"split\",\n" +
//                    "          \"nodeDependents\": [\n" +
//                    "            \"P1\"\n" +
//                    "          ],\n" +
//                    "          \"pluginName\": \"dsp-common-stream-split\",\n" +
//                    "          \"pluginConfig\": {\n" +
//                    "            \"splitNum\": 3,\n" +
//                    "            \"splitOuts\": [\n" +
//                    "              {\n" +
//                    "                \"filterMode\": \"js\",\n" +
//                    "                \"filterExpr\": \"\"\n" +
//                    "              },\n" +
//                    "              {\n" +
//                    "                \"filterMode\": \"match\",\n" +
//                    "                \"filterExpr\": \"a=123\"\n" +
//                    "              },\n" +
//                    "              {\n" +
//                    "                \"filterMode\": \"regexp\",\n" +
//                    "                \"filterExpr\": \"a=^adada&\"\n" +
//                    "              }\n" +
//                    "            ]\n" +
//                    "          }\n" +
//                    "        },\n" +
//                    "        \"P1\": {\n" +
//                    "          \"nodeId\": \"P1\",\n" +
//                    "          \"nodeName\": \"数据处理1\",\n" +
//                    "          \"nodeType\": \"Process\",\n" +
//                    "          \"nodeDependents\": [\n" +
//                    "            \"R1\"\n" +
//                    "          ],\n" +
//                    "          \"pluginName\": \"dsp-plugin-common-process\",\n" +
//                    "          \"pluginConfig\": {\n" +
//                    "          }\n" +
//                    "        },\n" +
//                    "        \"U1\": {\n" +
//                    "          \"nodeId\": \"U1\",\n" +
//                    "          \"nodeName\": \"合并节点1\",\n" +
//                    "          \"nodeType\": \"union\",\n" +
//                    "          \"nodeDependents\": [\n" +
//                    "            \"S1.0\",\n" +
//                    "            \"S1.1\",\n" +
//                    "            \"S1.2\"\n" +
//                    "          ],\n" +
//                    "          \"pluginName\": \"dsp-plugin-common-union\",\n" +
//                    "          \"pluginConfig\": \"\"\n" +
//                    "        },\n" +
//                    "        \"W1\": {\n" +
//                    "          \"nodeId\": \"W1\",\n" +
//                    "          \"nodeName\": \"输出节点1\",\n" +
//                    "          \"nodeType\": \"writer\",\n" +
//                    "          \"nodeDependents\": [\n" +
//                    "            \"U1\"\n" +
//                    "          ],\n" +
//                    "          \"pluginName\": \"dsp-plugin-common-writer\",\n" +
//                    "          \"pluginConfig\": {\n" +
//                    "          }\n" +
//                    "        }\n" +
//                    "      }\n" +
//                    "    },\n" +
//                    "    \"core\": {\n" +
//                    "      \"engineConfig\": {\n" +
//                    "        \"mode\": \"yarn-pre\",\n" +
//                    "        \"mode\": \"yarn-session\",\n" +
//                    "        \"mode\": \"yarn-application\",\n" +
//                    "        \"mode\": \"standalone\"\n" +
//                    "      },\n" +
//                    "      \"loggingConfig\": {\n" +
//                    "        \"level\": \"info\"\n" +
//                    "      },\n" +
//                    "      \"metricsConfig\": {\n" +
//                    "        \"reportServer\": \"127.0.0.1:8989\"\n" +
//                    "      },\n" +
//                    "      \"systemConfig\": {\n" +
//                    "        \"osName\": \"linux\"\n" +
//                    "      }\n" +
//                    "    },\n" +
//                    "    \"custom\": {\n" +
//                    "      \"a\": \"a\",\n" +
//                    "      \"b\": \"b\"\n" +
//                    "    }\n" +
//                    "  }\n" +
//                    "}");
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
