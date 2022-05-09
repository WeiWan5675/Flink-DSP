package com.weiwan.dsp.core.engine.snapshot;

import com.weiwan.dsp.api.pojo.DspState;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @description: 引擎快照接口(用户侧)
 */
public interface EngineSnapshotFacade extends Serializable {


    void addState(DspState state, Integer pluginId);

    DspState getState(Integer pluginId);

    EngineSnapshotFacade addSnapshotFailCallback(Integer pluginId, boolean tolerant, SnapshotFailCallback ... callback);

    EngineSnapshotFacade addSnapshotCallback(Integer pluginId, boolean tolerant, SnapshotCallback ... callback);

    boolean isRestore();
}
