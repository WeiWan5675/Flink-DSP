package com.weiwan.dsp.core.engine.snapshot;

import com.weiwan.dsp.api.pojo.DspState;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/11 19:14
 * @Package: com.weiwan.dsp.core.engine.snapshot
 * @ClassName: SnapshotFailCallback
 * @Description: 失败回调
 **/
@FunctionalInterface
public interface SnapshotFailCallback {
    void failCall(long snapshotId, DspState dspState);
}
