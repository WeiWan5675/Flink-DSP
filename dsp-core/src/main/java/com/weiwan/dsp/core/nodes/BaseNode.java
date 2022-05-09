package com.weiwan.dsp.core.nodes;

import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.context.DspSupport;
import com.weiwan.dsp.api.plugin.Plugin;

import java.util.*;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 13:26
 * @description:
 */
public abstract class BaseNode extends DspNode implements DspSupport {

    protected EngineContext context;
    protected List<Plugin> plugins = new ArrayList<>();

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public EngineContext getContext() {
        return this.context;
    }

    @Override
    public void setContext(EngineContext context) {
        this.context = context;
    }
}

