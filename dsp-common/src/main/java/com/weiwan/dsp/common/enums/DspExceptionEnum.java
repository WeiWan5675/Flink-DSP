/*
 *      Copyright [2020] [xiaozhennan1995@gmail.com]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *      http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weiwan.dsp.common.enums;

/**
 * @Author: xiaozhennan
 * @Date: 2020/4/28 9:13
 * @Package: com.hopson.dc.realtime.common.enums
 * @ClassName: DspExceptionEnum
 * @Description:
 **/
public enum DspExceptionEnum {
    //常用状态
    SUCCESS(00000, "成功"),
    FAILED(99999, "失败的"),
    //未知状态
    UNKNOWN(99998, "未知异常"),

    //参数状态
    PARAMETER_EMPTY(99997, "参数为空"),
    PARAMETER_ILLEGAL(99996, "参数非法"),
    PARAMETER_FORMAT_FAILED(99995, "参数格式化异常"),
    DATA_ERROR(99994, "数据错误"),
    CONFIG_ERROR(99993, "配置错误"),


    //系统异常
    SYS_ERROR(10000, "系统错误"),
    SYS_DB_ERROR(10001, "数据库错误"),
    SYS_CONN_ERROR(10002, "连接错误"),
    SYS_RPC_ERROR(10003, "RPC调用错误"),


    //应用异常
    APP_ERROR(20000, "应用错误"),
    APP_MSG_ERROR(20001, "应用消息错误"),
    STATE_ILLEGAL(20002, "状态非法");


    private String msg;
    private int code;

    DspExceptionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
