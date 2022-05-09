package com.weiwan.dsp.api.enums;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/26 22:31
 * @ClassName: MetricKey
 * @Description:
 **/
public enum MetricKey {


    INPUT_TOTAL_NUM("input_total_num", "总输入记录数", 1000, false, 1),
    INPUT_TOTAL_SUCC_NUM("input_total_succ_num", "总输入成功记录数", 1001, false, 1),
    INPUT_TOTAL_FAIL_NUM("input_total_fail_num", "总输入失败记录数", 1002, false, 1),
    INPUT_REAL_EPS("input_real_eps", "实时输入EPS", 1003, false, 2),
    INPUT_SPENT_TIME("input_spent_time", "平均读取耗时", 1004, false, 2),
    INPUT_QUEUE_SIZE("input_queue_size", "输入队列长度", 1005, false, 2),
    INPUT_PLUGIN_FAIL_NUM("input_plugin_fail_num-<pluginName>", "插件失败记录数", 1006, true, 1),
    INPUT_PLUGIN_SUCS_NUM("input_plugin_sucs_num-<pluginName>", "插件成功记录数", 1007, true, 1),


    PROCESS_TOTAL_NUM("process_total_num", "总处理记录数", 2000, false, 1),
    PROCESS_TOTAL_SUCS_NUM("process_total_succ_num", "总处理成功记录数", 2001, false, 1),
    PROCESS_TOTAL_FAIL_NUM("process_total_fail_num", "总处理失败记录数", 2002, false, 1),
    PROCESS_REAL_EPS("process_real_eps", "实时处理EPS", 2003, false, 2),
    PROCESS_SPENT_TIME("process_spent_time", "平均处理耗时", 2004, false, 2),
    PROCESS_PLUGIN_FAIL_NUM("process_plugin_fail_num-<pluginName>", "插件失败记录数", 2005, true, 1),
    PROCESS_PLUGIN_SUCS_NUM("process_plugin_sucs_num-<pluginName>", "插件成功记录数", 2006, true, 1),
    PROCESS_PLUGIN_SPENT_TIME("process_plugin_spent_time-<pluginName>", "插件处理耗时", 2007, true, 2),


    OUTPUT_TOTAL_NUM("output_total_num", "总接收记录数", 3000, false, 1),
    OUTPUT_INPUT_SUCS_NUM("output_input_succ_num", "总输出队列成功记录数", 3001, false, 1),
    OUTPUT_INPUT_FAIL_NUM("output_input_fail_num", "总输出队列失败记录数", 3002, false, 1),
    OUTPUT_REAL_EPS("output_real_eps", "实时输出EPS", 3003, false, 2),
    OUTPUT_SPENT_TIME("output_spent_time", "平均输出耗时", 3004, false, 2),
    OUTPUT_PLUGIN_QUEUE_SIZE("output_plugin_queue_size-<pluginName>", "插件输出队列长度", 3005, true, 2),
    OUTPUT_PLUGIN_REAL_EPS("output_plugin_real_eps-<pluginName>", "插件输出EPS", 3006, true, 2),
    OUTPUT_PLUGIN_SPENT_TIME("output_plugin_spent_time-<pluginName>", "插件输出耗时", 3007, true, 2),
    OUTPUT_PLUGIN_SUCS_NUM("output_plugin_sucs_num-<pluginName>", "插件输出成功记录数", 3008, true, 1),
    OUTPUT_PLUGIN_FAIL_NUM("output_plugin_fail_num-<pluginName>", "插件输出失败记录数", 3009, true, 1),


    SPLIT_TOTAL_NUM("split_total_num", "总处理记录数", 4000, false, 1),
    SPLIT_TOTAL_SUCS_NUM("split_total_sucs_num", "总处理成功记录数", 4001, false, 1),
    SPLIT_TOTAL_FAIL_NUM("split_total_fail_num", "总处理失败记录数", 4002, false, 1),
    SPLIT_REAL_EPS("split_real_eps", "总处理成功记录数", 4003, false, 1),
    SPLIT_SPENT_TIME("split_spent_time", "匹配耗时", 4004, false, 2),
    SPLIT_MATCHED_NUM("split_matched_num", "匹配记录数", 4005, false, 1),
    SPLIT_UNMATCHED_NUM("split_unmatched_num", "未匹配记录数", 4006, false, 1),
    SPLIT_PLUGIN_MATCHED_NUM("split_plugin_matched_num-<pluginName>", "插件匹配记录数", 4007, true, 1),
    SPLIT_PLUGIN_UNMATCHED_NUM("split_plugin_unmatched_num-<pluginName>", "插件未匹配记录数", 4008, true, 1),
    SPLIT_PLUGIN_MATCH_TIME("split_plugin_match_time-<pluginName>", "插件匹配耗时", 4009, true, 2);


    private static final String PLUGIN_NAME_PLACEHOLDER = "<pluginName>";

    private final String key;
    private final String msg;
    private final int code;
    private final boolean pMetric;
    private final int type;

    MetricKey(String key, String msg, int code, boolean pMetric, int type) {
        this.key = key;
        this.msg = msg;
        this.code = code;
        this.pMetric = pMetric;
        this.type = type;
    }

    public boolean ispMetric() {
        return pMetric;
    }

    public String getKey() {
        return key;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }


    public static String replacePN(MetricKey key, String pluginName) {
        return key.getKey().replaceAll(PLUGIN_NAME_PLACEHOLDER, pluginName);
    }


    @Override
    public String toString() {
        return "MetricKey{" +
                "key='" + key + '\'' +
                ", msg='" + msg + '\'' +
                ", code=" + code +
                '}';
    }
}
