package com.weiwan.dsp.core.engine.flink.checkpoint;

import org.apache.flink.api.common.state.CheckpointListener;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;

/**
 * @author: xiaozhennan
 * @date: 2021/6/30 16:11
 * @description: flink得checkpoint接口聚合
 */
public interface CheckpointInterface extends CheckpointedFunction, CheckpointListener {

}
