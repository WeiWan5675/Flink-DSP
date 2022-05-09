package com.weiwan.dsp.core.engine.snapshot;

/**
 * @author: xiaozhennan
 * @date: 2021/6/30 16:49
 * @description:
 * 1. 插件容器提供一个广播state的方法
 * 2. 插件容器提供一个收集state的方法
 * 3. state以节点+插件序列号为最小单位 节点STATE(算子) -> Plugin-NUM-STATE
 * 4. 非Flink框架插件,上传STATE无效
 * 5. 添加是否是有状态插件属性
 * 6.
 */
public class NodeState {

    //多个插件,每个插件都有一个state
    private Snapshot snapshot;
    private Integer numOfSubTask;


    public NodeState() {
    }

    public NodeState(Integer indexOfThisSubtask, Snapshot snapshot) {
        this.numOfSubTask = indexOfThisSubtask;
        this.snapshot = snapshot;
    }



    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Integer getNumOfSubTask() {
        return numOfSubTask;
    }

    public void setNumOfSubTask(Integer numOfSubTask) {
        this.numOfSubTask = numOfSubTask;
    }


    @Override
    public String toString() {
        return "NodeState{" +
                "snapshot=" + snapshot +
                ", numOfSubTask=" + numOfSubTask +
                '}';
    }
}
