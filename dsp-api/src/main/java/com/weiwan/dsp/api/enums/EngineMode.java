package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/5/24 10:28
 * @description:
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EngineMode implements Serializable {
    FLINK_ON_YARN_PER("flink_on_yarn_per", 1),
    FLINK_ON_YARN_SESSION("flink_on_yarn_session", 2),
    FLINK_ON_YARN_APPLICATION("flink_on_yarn_application", 3),
    FLINK_ON_K8S_APPLICATION("flink_on_k8s_application", 4),
    FLINK_ON_K8S_SESSION("flink_on_k8s_session", 5),
    FLINK_STANDALONE("flink_standalone", 6),
    SPARK_ON_YARN_PER("spark_on_yarn_per", 7),
    SPARK_ON_YARN_SESSION("spark_on_yarn_session", 8),
    SPARK_ON_K8S("spark_on_k8s", 9),
    LOCAL("local", 10);

    private final int code;
    private final String type;

    EngineMode(String type, int code) {
        this.type = type;
        this.code = code;
    }


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static EngineMode form(Object object) {
        if(object instanceof Map){
            return from((Map) object);
        }
        if(object instanceof String){
            return from((String) object);
        }
        return FLINK_STANDALONE;
    }

    public static EngineMode from(String name) {
        EngineMode[] values = values();
        String key = name.replaceAll("-", "_");
        for (EngineMode value : values) {
            if (value.type.trim().equalsIgnoreCase(key.trim())) {
                return value;
            }
        }
        return null;
    }


    public static EngineMode from(Map name) {
        Integer code = (Integer) name.get("code");
        EngineMode engineType = getEngineMode(code);
        return engineType;
    }

    public static EngineMode getEngineMode(Integer code) {
        EngineMode[] values = values();
        for (EngineMode value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }


    public static List<EngineMode> getFlinkEngineMode() {
        List<EngineMode> engineModes = new ArrayList<>();
        engineModes.add(FLINK_ON_YARN_PER);
        engineModes.add(FLINK_ON_YARN_SESSION);
        engineModes.add(FLINK_ON_YARN_APPLICATION);
        engineModes.add(FLINK_ON_K8S_APPLICATION);
        engineModes.add(FLINK_ON_K8S_SESSION);
        engineModes.add(FLINK_STANDALONE);
        return engineModes;
    }

    public static List<EngineMode> getLocalEngineMode() {
        List<EngineMode> engineModes = new ArrayList<>();
        engineModes.add(LOCAL);
        return engineModes;
    }


    public static List<EngineMode> getSparkEngineMode() {
        List<EngineMode> engineModes = new ArrayList<>();
        engineModes.add(SPARK_ON_YARN_PER);
        engineModes.add(SPARK_ON_YARN_SESSION);
        engineModes.add(SPARK_ON_K8S);
        return engineModes;
    }
}
