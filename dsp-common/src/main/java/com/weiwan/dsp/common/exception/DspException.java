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
package com.weiwan.dsp.common.exception;

import com.weiwan.dsp.common.enums.DspExceptionEnum;
import com.weiwan.dsp.common.enums.ResultStatus;

/**
 * @Author: xiaozhennan
 * @Date: 2020/4/27 17:24
 * @Package: com.hopson.dc.realtime.common.exception
 * @ClassName: DspException
 * @Description:
 **/
public class DspException extends RuntimeException {
    public int code;
    public String msg;

    public DspException(DspExceptionEnum exceptionEnum) {
        this.code = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMsg();
    }

    public DspException(Throwable e) {
        super(e);
    }

    public DspException() {
    }

    public DspException(DspExceptionEnum exceptionEnum, String msg) {
        super(msg);
        this.code = exceptionEnum.getCode();
        this.msg = msg;
    }

    public DspException(DspExceptionEnum exceptionEnum, String msg, Throwable e) {
        super(msg, e);
        this.code = exceptionEnum.getCode();
        this.msg = msg;
    }

    public DspException(String msg, Throwable e) {
        super(msg, e);
        this.code = DspExceptionEnum.FAILED.getCode();
        this.msg = msg;
    }

    public DspException(String msg) {
        super(msg);
        this.code = DspExceptionEnum.FAILED.getCode();
        this.msg = msg;
    }

    public DspException(String message, int code) {
        this.msg = message;
        this.code = code;
    }


    public DspException(ResultStatus status) {
        this.msg = status.getMessage();
        this.code = status.getCode();
    }

    public DspException(String msg, DspExceptionEnum exceptionEnum) {
        this.msg = msg;
        this.code = exceptionEnum.getCode();
    }

    public DspException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public static DspException generateParameterEmptyException(String msg) {
        return new DspException(DspExceptionEnum.PARAMETER_EMPTY, msg);
    }

    public static DspException generateUnknownException(String msg) {
        return new DspException(DspExceptionEnum.UNKNOWN, msg);
    }

    public static DspException generateParameterIllegalException(String msg) {
        return new DspException(DspExceptionEnum.PARAMETER_EMPTY, msg);
    }

    public static DspException generateDataException(String msg) {
        return new DspException(DspExceptionEnum.PARAMETER_EMPTY, msg);
    }


    public static DspException generateIllegalStateException(String msg) {
        return new DspException(DspExceptionEnum.STATE_ILLEGAL, msg);
    }

}
