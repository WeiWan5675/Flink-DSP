package com.weiwan.dsp.core.pub;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.enums.ConfigType;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @date: 2021/6/21 16:15
 * @description:
 */
public class DataSourceInfo implements Serializable {
    private ConfigType configType;
    private String sourceName;
    private boolean encrypt;
    private String encryptAlgorithm;

    public DataSourceInfo() {
    }

    public DataSourceInfo(String sourceName, ConfigType configType) {
        this.sourceName = sourceName;
        this.configType = configType;
    }


    public ConfigType getSourceType() {
        return configType;
    }

    public void setSourceType(ConfigType configType) {
        this.configType = configType;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public String getEncryptAlgorithm() {
        return encryptAlgorithm;
    }

    public void setEncryptAlgorithm(String encryptAlgorithm) {
        this.encryptAlgorithm = encryptAlgorithm;
    }

    public DataSourceInfo(String sourceName, ConfigType configType, boolean encrypt, String encryptAlgorithm) {
        this.sourceName = sourceName;
        this.configType = configType;
        this.encrypt = encrypt;
        this.encryptAlgorithm = encryptAlgorithm;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
