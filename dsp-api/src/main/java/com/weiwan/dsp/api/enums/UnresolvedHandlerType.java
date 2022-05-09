package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 16:15
 * @description:
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UnresolvedHandlerType {
    HTTP_HANDLER("Http收集器", "com.weiwan.dsp.core.resolve.http.HttpUnresolvedDataCollector", 1),
    JDBC_HANDLER("Jdbc收集器", "com.weiwan.dsp.core.resolve.jdbc.JDBCUnresolvedDataCollector", 2),
    KAFKA_HANDLER("Kafka收集器", "com.weiwan.dsp.core.resolve.kafka.KafkaUnresolvedDataCollector", 3),
    LOG_HANDLER("日志收集器", "com.weiwan.dsp.core.resolve.logging.LogUnresolvedDataCollector", 4);

    private final int code;
    private final String handlerClass;
    private final String type;

    UnresolvedHandlerType(String type, String handlerClass, int code) {
        this.type = type;
        this.handlerClass = handlerClass;
        this.code = code;
    }


    public static UnresolvedHandlerType getUnresolvedCollector(int unresolvedCollectorCode) {
        UnresolvedHandlerType[] values = values();
        if (unresolvedCollectorCode != 0) {
            for (UnresolvedHandlerType value : values) {
                if (value.getCode() == unresolvedCollectorCode) {
                    return value;
                }
            }
        }
        return null;
    }


    public static UnresolvedHandlerType from(String className){
        UnresolvedHandlerType[] values = values();
        for (UnresolvedHandlerType value : values) {
            if(value.handlerClass.equalsIgnoreCase(className)){
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static UnresolvedHandlerType from(Object object) {
        if(object instanceof Map){
            return from((Map) object);
        }
        if(object instanceof String){
            return from((String) object);
        }
        return null;
    }

    public static UnresolvedHandlerType from(Map name) {
        Integer code = (Integer) name.get("code");
        UnresolvedHandlerType engineType = getEngineType(code);
        return engineType;
    }

    public static UnresolvedHandlerType getEngineType(Integer code){
        if(code == null) return null;
        UnresolvedHandlerType[] values = values();
        for (UnresolvedHandlerType value : values) {
            if(value.code == code){
                return value;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getHandlerClass() {
        return handlerClass;
    }

    public String getType() {
        return type;
    }
}
