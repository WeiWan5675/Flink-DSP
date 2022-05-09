package com.weiwan.dsp.core.engine.ext;

import com.weiwan.dsp.core.engine.snapshot.EngineSnapshotFacade;

/**
 * @author: xiaozhennan
 * @description: 计算引擎得扩展能力,比如flink的checkpoint
 */
public interface EngineExtSupport {


    public EngineSnapshotFacade getEngineSnapshotFacde();

    public EngineExtInfo getEngineExtInfo();

}
