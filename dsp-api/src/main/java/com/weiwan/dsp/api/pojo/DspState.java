package com.weiwan.dsp.api.pojo;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @date: 2021/6/30 16:57
 * @description:
 */
public class DspState<T extends Serializable> implements Serializable{
    private T state;
    private Integer id;
    public DspState(T state) {
    }


    public T getValue() {
        return state;
    }

    public void update(T state) {
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
