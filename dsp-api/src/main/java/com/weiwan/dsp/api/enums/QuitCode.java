package com.weiwan.dsp.api.enums;

public enum QuitCode {
    ERROR(9999, "失败"),
    ENV_CHECK_FAILED(9998, "环境检查不通过"),
    CONFIG_PARSING_ERROR(9997, "配置解析错误"),
    CMD_PARSING_ERROR(9996, "命令行解析错误"),
    JOB_SUBMIT_ERROR(9995, "作业提交错误"),
    JOB_TYPE_PARSING_ERROR(9994, "作业类型解析错误"),
    SUCCESS(0, "作业提交成功");
    private final int code;
    private final String msg;

    QuitCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
