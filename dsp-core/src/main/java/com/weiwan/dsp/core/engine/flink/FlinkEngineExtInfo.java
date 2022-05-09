package com.weiwan.dsp.core.engine.flink;

import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.core.engine.ext.EngineExtInfo;

/**
 * @author: xiaozhennan
 * @description:
 */
public class FlinkEngineExtInfo implements EngineExtInfo {
    private EngineType engineType;
    private EngineMode engineMode;
    private int totalTaskNum;
    private int thisTaskNum;

    public FlinkEngineExtInfo(EngineType engineType, EngineMode engineMode) {
        this.engineType = engineType;
        this.engineMode = engineMode;
    }

    public int getTotalTaskNum() {
        return totalTaskNum;
    }

    public void setTotalTaskNum(int totalTaskNum) {
        this.totalTaskNum = totalTaskNum;
    }

    public int getThisTaskNum() {
        return thisTaskNum;
    }

    public void setThisTaskNum(int thisTaskNum) {
        this.thisTaskNum = thisTaskNum;
    }

    @Override
    public EngineType getEngineType() {
        return this.engineType;
    }

    @Override
    public EngineMode getEngineMode() {
        return this.engineMode;
    }
}
