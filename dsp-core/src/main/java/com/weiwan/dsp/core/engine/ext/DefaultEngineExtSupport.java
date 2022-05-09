package com.weiwan.dsp.core.engine.ext;


import com.weiwan.dsp.core.engine.snapshot.EngineSnapshotFacade;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @date: 2021/6/22 15:36
 * @description:
 */
public class DefaultEngineExtSupport implements EngineExtSupport, Serializable {

    private EngineExtInfo engineExtInfo;
    private EngineSnapshotFacade engineSnapshotFacade;

    public DefaultEngineExtSupport(EngineExtInfo engineExtInfo, EngineSnapshotFacade stateFacade) {
        this.engineExtInfo = engineExtInfo;
        this.engineSnapshotFacade = stateFacade;
    }


    @Override
    public EngineSnapshotFacade getEngineSnapshotFacde() {
        return this.engineSnapshotFacade;
    }


    @Override
    public EngineExtInfo getEngineExtInfo() {
        return this.engineExtInfo;
    }
}
