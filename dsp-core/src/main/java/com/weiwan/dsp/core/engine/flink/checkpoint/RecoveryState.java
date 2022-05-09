package com.weiwan.dsp.core.engine.flink.checkpoint;

import com.weiwan.dsp.core.engine.snapshot.NodeState;
import org.apache.flink.api.common.state.ListState;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description: 对ListState做包装
 */
public class RecoveryState {
    private ListState<NodeState> listState;
    private Map<Integer, NodeState> cacheMapStates = new HashMap<>();
    private volatile boolean isRestore;

    public ListState<NodeState> getListState() {
        return listState;
    }

    public void setListState(ListState<NodeState> listState) {
        this.listState = listState;
    }

    public boolean isRestore() {
        return isRestore;
    }

    public void setRestore(boolean restore) {
        isRestore = restore;
    }

    public void clearState() {
        if (listState != null) {
            listState.clear();
        }
    }

    public void addState(NodeState nodeState) throws Exception {
        if (listState != null) {
            listState.add(nodeState);
        }
    }

    public Map<Integer, NodeState> getCacheMapStates() {
        return cacheMapStates;
    }

    public void setCacheMapStates(Map<Integer, NodeState> cacheMapStates) {
        this.cacheMapStates = cacheMapStates;
    }
}
