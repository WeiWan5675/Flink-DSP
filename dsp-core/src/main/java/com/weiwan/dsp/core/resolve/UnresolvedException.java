package com.weiwan.dsp.core.resolve;

import com.weiwan.dsp.api.enums.UnresolvedType;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.common.exception.DspException;

/**
 * @author: xiaozhennan
 * @date: 2021/6/22 15:57
 * @description: 未解析数据的异常包裹类
 */
public class UnresolvedException extends DspException {
    private UnresolvedType unresolvedType;
    private DataRecord unresolvedRecord;
    private Throwable throwable;

    public UnresolvedException(UnresolvedType unresolvedType, DataRecord unresolvedRecord, Throwable throwable) {
        super(throwable);
        this.unresolvedType = unresolvedType;
        this.unresolvedRecord = unresolvedRecord;
        this.throwable = throwable;
    }

    public UnresolvedType getUnresolvedType() {
        return unresolvedType;
    }

    public void setUnresolvedType(UnresolvedType unresolvedType) {
        this.unresolvedType = unresolvedType;
    }

    public DataRecord getUnresolvedRecord() {
        return unresolvedRecord;
    }

    public void setUnresolvedRecord(DataRecord unresolvedRecord) {
        this.unresolvedRecord = unresolvedRecord;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
