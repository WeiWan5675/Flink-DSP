package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.common.config.AbstractConfig;
import com.weiwan.dsp.common.config.ConfigOption;

import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public class LocalConfigs extends Configs {
    public LocalConfigs(Map<String, Object> configs) {
        super(configs);
    }

    public LocalConfigs() {
        super();
    }

    @Override
    public void loadOptions(List<ConfigOption> options) {

    }
}
