package com.weiwan.dsp.core.pub;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/12 15:01
 * @description:
 */
public class SpeedLimiter {

    private RateLimiter rateLimiter;
    private Double tokenPerSecond;
    private Long maxCount;
    private Long interval;
    private boolean enableSpeed;

    public SpeedLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public SpeedLimiter(Long maxCount, Long interval, TimeUnit timeUnit) {
        this((maxCount.doubleValue() / TimeUnit.SECONDS.convert(interval, timeUnit)));
        this.interval = interval;
        this.maxCount = maxCount;
    }

    public SpeedLimiter(Double maxCount) {
        this.tokenPerSecond = maxCount;
        this.rateLimiter = RateLimiter.create(tokenPerSecond);
    }

    public void limit() {
        if (enableSpeed) {
            rateLimiter.acquire();
        }
    }

    public void limit(int count) {
        if (enableSpeed) {
            rateLimiter.acquire(count);
        }
    }


    public long getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(long maxCount) {
        this.maxCount = maxCount;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean isEnableSpeed() {
        return enableSpeed;
    }

    public void setEnableSpeed(boolean enableSpeed) {
        this.enableSpeed = enableSpeed;
    }
}
