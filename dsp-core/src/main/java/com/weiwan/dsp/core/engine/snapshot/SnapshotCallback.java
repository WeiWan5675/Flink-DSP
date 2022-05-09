package com.weiwan.dsp.core.engine.snapshot;

import com.weiwan.dsp.api.pojo.DspState;

/**
 * @author: xiaozhennan
 * @description: 快照回调接口
 */
@FunctionalInterface
public interface SnapshotCallback {
    void call(long snapshotId, DspState dspState);
}
