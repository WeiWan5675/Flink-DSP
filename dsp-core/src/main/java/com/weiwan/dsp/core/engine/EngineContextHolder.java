package com.weiwan.dsp.core.engine;

import com.weiwan.dsp.api.context.EngineContext;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 10:10
 * @description: 获取DspContext
 */
public class EngineContextHolder {

    private final InheritableThreadLocal<EngineContext> threadLocal = new InheritableThreadLocal<EngineContext>();

    private volatile static EngineContextHolder instance;
    private volatile boolean inited;
    private EngineContextHolder() {
    }

    private static EngineContextHolder getInstance() {
        if (instance == null) {
            synchronized (EngineContextHolder.class) {
                if (instance == null) {
                    instance = new EngineContextHolder();
                }
            }
        }
        return instance;
    }

    public void initContext(EngineContext context) {
        if (context != null) {
            threadLocal.set(context);
            inited = true;
        }
    }

    private final EngineContext getInternalContext() {
        if(!inited) throw new RuntimeException("The context object is not initialized.");
        return threadLocal.get();
    }

    public static final EngineContext getContext() {
        return EngineContextHolder.getInstance().getInternalContext();
    }

    public static synchronized final void init(EngineContext context) {
        EngineContextHolder.getInstance().initContext(context);
    }
}
