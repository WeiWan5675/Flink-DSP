package com.weiwan.dsp.core.engine.snapshot;

import com.weiwan.dsp.api.pojo.DspState;
import com.weiwan.dsp.common.exception.DspException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaozhennan
 * @description: 插件快照门面类
 */
public class DefaultEngineSnapshot implements EngineSnapshotFacade, EngineSnapshot {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEngineSnapshot.class);
    private Snapshot snapshot = new Snapshot();
    private Snapshot lastSnapshot;
    private List<SnapshotCallbackWarp> callbacks = new ArrayList<>();
    private List<SnapshotFailCallbackWarp> failCallbacks = new ArrayList<>();
    private boolean isRestore;

    @Override
    public synchronized void initSnapshot(Snapshot snapshot, boolean isRestore) {
        if (snapshot != null) {
            this.snapshot = snapshot;
            this.isRestore = isRestore;
        }else{
            this.snapshot = new Snapshot();
            this.isRestore = false;
        }
    }

    @Override
    public synchronized Snapshot getSnapshot() {
        final Snapshot tmpSnapshot = snapshot;
        this.snapshot = new Snapshot();
        this.snapshot.setStates(tmpSnapshot.getStates());
        this.lastSnapshot = tmpSnapshot;
        return tmpSnapshot;
    }


    @Override
    public void notifySuccess(long snapshotId) {
        if (callbacks != null && callbacks.size() > 0) {
            synchronized (this) {
                for (SnapshotCallbackWarp callback : callbacks) {
                    try {
                        DspState state;
                        state = lastSnapshot.get(callback.getPluginId());
                        if (state != null) {
                            callback.getCallback().call(snapshotId, state);
                        }
                    } catch (Exception e) {
                        if (callback.isTolerant()) {
                            logger.warn(String.format("snapshot success callback fail, snapshot id: %s", snapshotId), e);
                        } else {
                            throw new RuntimeException(String.format("snapshot successCallback fail, snapshot id: %s", snapshotId), e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void notifyFail(long snapshotId) {
        if (callbacks != null && callbacks.size() > 0) {
            synchronized (this) {
                for (SnapshotFailCallbackWarp callback : failCallbacks) {
                    try {
                        DspState state;
                        state = lastSnapshot.get(callback.getPluginId());
                        if (state != null) {
                            callback.getCallback().failCall(snapshotId, state);
                        }
                    } catch (Exception e) {
                        if (callback.isTolerant()) {
                            logger.warn(String.format("snapshot failCallback fail, snapshot id: %s", snapshotId), e);
                        } else {
                            throw new RuntimeException(String.format("snapshot failCallback fail, snapshot id: %s", snapshotId), e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public synchronized void addState(DspState state ,Integer pluginId) {
        if (pluginId != null) {
            state.setId(pluginId);
        }else{
            throw DspException.generateIllegalStateException("To add status, you must specify a plug-in ID, which can be obtained through getPluginId()!");
        }
        this.snapshot.add(state);
    }

    @Override
    public synchronized DspState getState(Integer id) {
        return this.snapshot.get(id);
    }

    @Override
    public EngineSnapshotFacade addSnapshotFailCallback(Integer pluginId, boolean tolerant, SnapshotFailCallback ... snapshotFailCallbacks) {
        if (snapshotFailCallbacks != null) {
            synchronized (this) {
                for (SnapshotFailCallback failCallback: snapshotFailCallbacks) {
                    SnapshotFailCallbackWarp snapshotCallbackWarp = new SnapshotFailCallbackWarp(failCallback, pluginId, tolerant);
                    failCallbacks.add(snapshotCallbackWarp);
                }
            }
        }
        return this;
    }

    @Override
    public EngineSnapshotFacade addSnapshotCallback(Integer pluginId, boolean tolerant, SnapshotCallback ... snapshotCallbacks) {
        if (snapshotCallbacks != null) {
            synchronized (this) {
                for (SnapshotCallback snapshotCallback : snapshotCallbacks) {
                    SnapshotCallbackWarp snapshotCallbackWarp = new SnapshotCallbackWarp(snapshotCallback, pluginId, tolerant);
                    callbacks.add(snapshotCallbackWarp);
                }
            }
        }
        return this;
    }


    class SnapshotCallbackWarp {
        private SnapshotCallback callback;
        private Integer pluginId;
        private boolean tolerant;

        public SnapshotCallbackWarp(SnapshotCallback callback, Integer pluginId, boolean tolerant) {
            this.callback = callback;
            this.pluginId = pluginId;
            this.tolerant = tolerant;
        }

        public SnapshotCallback getCallback() {
            return callback;
        }

        public void setCallback(SnapshotCallback callback) {
            this.callback = callback;
        }

        public boolean isTolerant() {
            return tolerant;
        }

        public void setTolerant(boolean tolerant) {
            this.tolerant = tolerant;
        }

        public Integer getPluginId() {
            return pluginId;
        }

        public void setPluginId(Integer pluginId) {
            this.pluginId = pluginId;
        }
    }
    class SnapshotFailCallbackWarp {
        private SnapshotFailCallback callback;
        private Integer pluginId;
        private boolean tolerant;

        public SnapshotFailCallbackWarp(SnapshotFailCallback callback, Integer pluginId, boolean tolerant) {
            this.callback = callback;
            this.pluginId = pluginId;
            this.tolerant = tolerant;
        }

        public SnapshotFailCallback getCallback() {
            return callback;
        }

        public void setCallback(SnapshotFailCallback callback) {
            this.callback = callback;
        }

        public boolean isTolerant() {
            return tolerant;
        }

        public void setTolerant(boolean tolerant) {
            this.tolerant = tolerant;
        }

        public Integer getPluginId() {
            return pluginId;
        }

        public void setPluginId(Integer pluginId) {
            this.pluginId = pluginId;
        }
    }
    @Override
    public boolean isRestore() {
        return this.isRestore;
    }
}
