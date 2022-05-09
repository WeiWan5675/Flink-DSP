package com.weiwan.dsp.core.engine.snapshot;

import com.weiwan.dsp.api.pojo.DspState;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xiaozhennan
 * @date: 2021/6/30 16:50
 * @description:
 */
public class Snapshot implements Serializable {
    private Map<Integer, DspState> states = new ConcurrentHashMap<>();

    public synchronized Map<Integer, DspState> getStates() {
        return states;
    }

    public synchronized void setStates(Map<Integer, DspState> states) {
        this.states = states;
    }

    public synchronized void add(DspState dspState) {
        if (dspState != null) {
            states.put(dspState.getId(), dspState);
        }
    }

    public DspState get(Integer id) {
        return states.get(id);
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "states=" + states +
                '}';
    }
}
