package com.weiwan.dsp.common.exception;

import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.enums.ResultStatus;
import com.weiwan.dsp.common.exception.DspException;

public class DspConsoleException extends DspException {

    ResultStatus status;

    public DspConsoleException(ResultStatus status) {
        //不生成栈追踪信息
        super(status.getMessage(), status.getCode());
        this.status = status;
    }


    public DspConsoleException(int code, String msg) {
        super(code, msg);
    }

    public DspConsoleException(DspResultStatus error, String msg) {
        this(error);
        this.msg = msg;
    }


    public static DspConsoleException generateIllegalStateException(DspResultStatus status) {
        return new DspConsoleException(status);
    }


    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }


}
