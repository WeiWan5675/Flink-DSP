package com.weiwan.dsp.console.service.impl.deploy;

/**
 * @author: xiaozhennan
 * @description:
 */
public enum ApplicationEventType {
    START("Start", 0),
    STOP("Stop", 1),
    CANCEL("Cancel", 2),
    REFRESH("Refresh", 3);
    private final String type;
    private final int code;

    ApplicationEventType(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }
}
