package com.weiwan.dsp.console.model.vo;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @description:
 */
public class TemplateVo implements Serializable {
    public static final String REF_DICT_API_FORMAT = "REF:DICT:/dict/%s";
    public static final String REF_TEMPLATE_API_FORMAT = "REF:TEMPLATE:/template/%s";


    public String getDictApi(String api) {
        return String.format(REF_DICT_API_FORMAT, api);
    }


    public String getTemplateApi(String api) {
        return String.format(REF_TEMPLATE_API_FORMAT, api);
    }
}
