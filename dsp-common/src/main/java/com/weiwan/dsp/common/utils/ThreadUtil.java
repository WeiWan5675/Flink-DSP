package com.weiwan.dsp.common.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author: xiaozhennan
 * @description:
 */
public class ThreadUtil {

    public static ThreadFactory threadFactory(String threadName, boolean isDaemon) {
        return new ThreadFactoryBuilder().setNameFormat(String.format("thread-%s", threadName)).setDaemon(isDaemon).build();
    }

    public static ThreadFactory threadFactory(String threadName, boolean isDaemon, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        return new ThreadFactoryBuilder().setUncaughtExceptionHandler(uncaughtExceptionHandler).setNameFormat(String.format("thread-%s", threadName)).setDaemon(isDaemon).build();
    }

    public static ThreadFactory threadFactory(String threadName) {
        return threadFactory(threadName, false, getLogUncaughtExceptionHandler(threadName));
    }


    public static void shutdownExecutorService(ExecutorService executorService) throws InterruptedException {
        shutdownExecutorService(executorService, 8, TimeUnit.SECONDS);
    }

    public static void shutdownExecutorService(ExecutorService executorService, long timeout, TimeUnit timeUnit) throws InterruptedException {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            if (!executorService.awaitTermination(timeout, timeUnit)) {
                executorService.shutdownNow();
                executorService.awaitTermination(timeout, timeUnit);
            }
        }
    }


    public static Thread.UncaughtExceptionHandler getLogUncaughtExceptionHandler(String name) {
        if (StringUtils.isNotBlank(name)) {
            name = "unknown";
        }
        return new LogUncaughtExceptionHandler(name);
    }


    static class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        private static final Logger LOGGER = LoggerFactory.getLogger(LogUncaughtExceptionHandler.class);
        private String name;

        public LogUncaughtExceptionHandler(String name) {
            this.name = name;
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOGGER.error(name + " caught thread exception : " + t.getName(), e);
        }
    }
}
