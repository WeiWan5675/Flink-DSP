package com.weiwan.dsp.core.pub;

import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.plugin.Launcher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/26 11:01
 * @description:
 */
public class DataLauncher<T> implements Launcher<T> {

    private BlockingQueue<T> queue;
    private SpeedLimiter speedLimiter;
    private EngineContext context;
    private volatile boolean isOpen = true;

    public DataLauncher(BlockingQueue<T> queue, SpeedLimiter speedLimiter) {
        this.queue = queue;
        this.speedLimiter = speedLimiter;
    }

    @Override
    public void launch(T obj) throws Exception {
        speedLimiter.limit();
        queue.offer(obj, 10, TimeUnit.SECONDS);
    }

    @Override
    public void launch(List<T> objs) throws Exception {
        speedLimiter.limit(objs.size());
        queue.addAll(objs);
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }


    @Override
    public void setContext(EngineContext context) {
        this.context = context;
    }

    @Override
    public void close() throws IOException {
        this.isOpen = false;
    }
}
