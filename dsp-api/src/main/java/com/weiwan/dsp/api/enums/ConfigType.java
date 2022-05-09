package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public enum ConfigType {
    // ConfigType = Database
    MYSQL("MySql", 1001),
    ORACLE("Oracle", 1002),
    REDIS("Redis", 1003),
    HBASE("Hbase", 1004),
    MONGO_DB("MongoDb", 1005),
    POSTGRE_SQL("PostgreSql", 1006),
    SQL_SERVER("SqlServer", 1007),
    CLICKHOUSE("ClickHouse", 1008),
    DB2("DB2", 1009),


    //ConfigType = Service
    FTP("Ftp", 2001),
    KAFKA("kafka", 2002),
    HDFS("Hdfs", 2003),
    FLINK("Flink", 2004),
    YARN("Yarn", 2005),
    ZOOKEEPER("Zookeeper", 2006),
    S3("S3", 2007),
    ROCKET_MQ("RocketMQ", 2008),
    ACTIVE_MQ("ActiveMQ", 2009),
    RABBIT_MQ("RabbitMQ", 2010),
    ELASTICSEARCH("Elasticsearch", 2011),
    SOLR("Solr", 2012),

    //ConfigType = Plugin
    INPUT_PLUGIN("InputPlugin", 3001),
    OUTPUT_PLUGIN("OutputPlugin", 3002),
    SPLIT_PLUGIN("SplitPlugin", 3003),
    PROCESS_PLUGIN("ProcessPlugin", 3004),
    UNION_PLUGIN("UnionPlugin", 3005),

    //ConfigType = Other
    FILE_SERVER("FileServer", 4001),
    JDBC("Jdbc", 4002),
    RDBMS("Rdbms", 4003),
    MSG_QUEUE("MsgQueue", 4004),
    RPC("Rpc", 4005),
    HTTP("Http", 4006),
    KERBEROS("Kerberos", 4007),
    DATABASE("Database", 4008),
    NOSQL("NoSql", 4009),


    UNKNOWN("Unknown", 9999);
    private final String type;
    private final int code;

    ConfigType(String type, int code) {
        this.type = type;
        this.code = code;
    }


    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }


    public static ConfigType from(Integer code) {
        if (code == null) return ConfigType.UNKNOWN;
        ConfigType[] values = values();
        for (ConfigType value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ConfigType from(Object object) {
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

    public static ConfigType from(String name) {
        ConfigType[] values = values();
        for (ConfigType value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }


    public static ConfigType from(Map<String, String> name) {
        Integer code = Integer.parseInt(name.get("code"));
        ConfigType ConfigType = getConfigType(code);
        return ConfigType;
    }

    public static ConfigType getConfigType(Integer code) {
        ConfigType[] values = values();
        for (ConfigType value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
