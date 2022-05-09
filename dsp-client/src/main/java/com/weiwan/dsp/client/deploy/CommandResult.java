package com.weiwan.dsp.client.deploy;


/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/5/24 13:45
 * @description:
 */
public class CommandResult {
    private boolean success;
    private String msg;
    private Throwable throwable;

    public CommandResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public CommandResult(boolean success, String msg, Exception e) {
        this.success = success;
        this.msg = msg;
        this.throwable = e;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMsg() {
        return this.msg;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}
