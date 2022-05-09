package com.weiwan.dsp.console.model;

import com.weiwan.dsp.common.enums.ResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import lombok.Builder;
import lombok.Data;
import com.weiwan.dsp.common.enums.DspResultStatus;

import java.io.Serializable;

/**
 * @author: xiaozhennan@outlook.com
 */
@Data
@Builder
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -1319823219827491479L;

    private boolean success;

    private int code;

    private String message;
    private T result;

    private static final Result EMPTY_SUCCESS_RESULT = Result.success(null);

    public static <T> Result<T> success() {
        return EMPTY_SUCCESS_RESULT;
    }

    public static <T> Result<T> success(T obj) {
        ResultBuilder<T> resultBuilder = new ResultBuilder<T>();
        return resultBuilder
                .result(obj)
                .code(DspResultStatus.OK.getCode())
                .message(DspResultStatus.OK.getMessage())
                .success(true).build();
    }

    public static Result failure(ResultStatus resultStatus) {
        return Result.builder()
                .success(false)
                .code(resultStatus.getCode())
                .message(resultStatus.getMessage()).build();
    }

    public static Result failure(ResultStatus resultStatus, DspConsoleException e) {
        return Result.builder()
                .success(false)
                .code(resultStatus.getCode())
                .message(e.getMsg()).build();
    }

    public static Result failure(ResultStatus resultStatus, Throwable e) {
        return Result.builder()
                .success(false)
                .result(e)
                .code(resultStatus.getCode())
                .message(resultStatus.getMessage()).build();
    }

    public static Result failure(ResultStatus resultStatus, String message) {
        return Result.builder()
                .success(false)
                .code(resultStatus.getCode())
                .message(message).build();
    }


}
