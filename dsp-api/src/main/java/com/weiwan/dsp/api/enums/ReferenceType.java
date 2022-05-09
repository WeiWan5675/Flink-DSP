package com.weiwan.dsp.api.enums;

/**
     * @author: xiaozhennan
     * @description:
     */
    public enum ReferenceType {
        APPLICATION_REF("应用引用", 1),
        FLOW_REF("流程引用", 2),
        PLUGIN_REF("插件引用", 3),
        APPLICATION_DEPLOY_REF("部署引用", 4),
        CONFIG_REF("配置引用", 5);
    
        private final int code;
        private final String type;
    
        ReferenceType(String type, int code) {
            this.type = type;
            this.code = code;
        }


    public Integer getCode() {
        return this.code;
    }

    public String getType(){
            return this.type;
    }
}