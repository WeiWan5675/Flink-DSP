package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApplicationState {

    RUNNING("运行", 1),
    //正确最终态
    STOPPED("停止", 2),
    CANCELED("取消", 3),
    FINISHED("完成", 4),

    //中间态
    STARTING("启动中", 5),
    STOPPING("停止中", 6),
    CANCELING("取消中", 7),
    RESTARTING("重启中", 8),

    //错误最终态
    FAILED("失败", 9),
    ERROR("错误", 10),

    UNKNOWN("未知", 11),

    INIT("初始化", 12),
    DISABLE("已禁用", 13),
    WAITING("等待中", 14);

    private final String state;
    private final int code;

    ApplicationState(String state, int code) {
        this.state = state;
        this.code = code;
    }


    public static ApplicationState from(Integer code) {
        if (code == null) return ApplicationState.UNKNOWN;
        ApplicationState[] values = values();
        for (ApplicationState value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ApplicationState from(Object object) {
        if (object == null) return UNKNOWN;
        if (object instanceof Map) {
            return from((Map) object);
        }
        if (object instanceof String) {
            return from((String) object);
        }
        if (object instanceof Integer) {
            return from((Integer) object);
        }
        return UNKNOWN;
    }

    public static ApplicationState from(String name) {
        ApplicationState[] values = values();
        for (ApplicationState value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }


    public static ApplicationState from(Map name) {
        Integer code = (Integer) name.get("code");
        ApplicationState engineType = getApplicationState(code);
        return engineType;
    }

    public static ApplicationState getApplicationState(Integer code) {
        if (code == null) {
            return ApplicationState.UNKNOWN;
        }
        ApplicationState[] values = values();
        for (ApplicationState value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }


    public static boolean isFinalState(ApplicationState applicationState) {
        if (applicationState != null) {
            switch (applicationState) {
                case STOPPED:
                case CANCELED:
                case FINISHED:
                case FAILED:
                case ERROR:
                case DISABLE:
                case INIT:
                case UNKNOWN:
                    return true;
            }
        }
        return false;
    }

    public static boolean isRunningState (ApplicationState applicationState) {
        if (applicationState != null) {
            switch (applicationState) {
                case RUNNING:
                case STARTING:
                case RESTARTING:
                case STOPPING:
                case CANCELING:
                case UNKNOWN:
                case WAITING:
                    return true;
            }
        }
        return false;
    }

    public static List<ApplicationState> getFinalStates() {
        ArrayList<ApplicationState> finalStates = new ArrayList<>();
        finalStates.add(STOPPED);
        finalStates.add(CANCELED);
        finalStates.add(FINISHED);
        finalStates.add(FAILED);
        finalStates.add(ERROR);
        finalStates.add(INIT);
        return finalStates;
    }

    public static List<ApplicationState> getRunningStates() {
        ArrayList<ApplicationState> runningStates = new ArrayList<>();
        runningStates.add(RUNNING);
        runningStates.add(STARTING);
        runningStates.add(STOPPING);
        runningStates.add(CANCELING);
        runningStates.add(RESTARTING);
        runningStates.add(UNKNOWN);
        runningStates.add(WAITING);
        return runningStates;
    }

    public String getState() {
        return state;
    }

    public int getCode() {
        return code;
    }
}
