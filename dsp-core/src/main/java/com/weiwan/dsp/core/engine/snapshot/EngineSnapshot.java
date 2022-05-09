package com.weiwan.dsp.core.engine.snapshot;

/**
 * @author: xiaozhennan
 * @description: 引擎快照接口(Engine侧)
 */
public interface EngineSnapshot {

    void notifySuccess(long snapshotId);

    void notifyFail(long snapshotId);

    void initSnapshot(Snapshot states, boolean isRestore);

    Snapshot getSnapshot();


}
