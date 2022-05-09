package com.weiwan.dsp.plugins.config;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/4/11 15:48
 * @description
 */
public class FieldRenamePluginConfig extends PluginConfig {
    private static final ConfigOption<List> RENAME_FIELDS = ConfigOptions.key("renameFields")
            .defaultValue(new ArrayList<>())
            .required(true)
            .textarea(true)
            .description("需修改的字段列表")
            .ok(List.class);

    public List getRenameFields() {
        return this.getVal(RENAME_FIELDS);
    }
}
