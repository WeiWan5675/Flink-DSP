package com.weiwan.dsp.console.schedule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/22 18:49
 * @description
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskType implements Serializable {
    SYSTEM_TASK(1, "System Task"),
    USER_TASK(2, "User Task");

    private Integer code;
    private String type;

    TaskType(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public String getType() {
        return type;
    }


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TaskType from(Object object) {
        if (object instanceof Map) {
            return from((Map) object);
        }
        if (object instanceof String) {
            return from((String) object);
        }
        return null;
    }

    public static TaskType from(Map map) {
        Integer code = (Integer) map.get("code");
        TaskType TaskType = getTaskType(code);
        return TaskType;
    }

    public static TaskType from(String type) {
        TaskType[] values = values();
        for (TaskType value : values) {
            if (value.name().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    public static TaskType getTaskType(Integer code) {
        TaskType[] types = values();
        for (TaskType type : types) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
