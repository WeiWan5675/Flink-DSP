package com.weiwan.dsp.core.pub;

/**
 * @author xiaozhennan
 */
public class DspContext {

    private static final InheritableThreadLocal<Context> threadLocal = new InheritableThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            return new Context();
        }
    };

    private static class ContextHolder {
        private final static DspContext CLIENT_CONTEXT = new DspContext();
    }

    public static DspContext getInstance() {
        return ContextHolder.CLIENT_CONTEXT;
    }

    public Context getContext() {
        return threadLocal.get();
    }

}