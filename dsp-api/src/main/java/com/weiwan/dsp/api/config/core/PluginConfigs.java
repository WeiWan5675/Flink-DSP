package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.common.config.ConfigOption;

import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public class PluginConfigs extends Configs {

    public PluginConfigs(Map m) {
        super(m);
    }

    public PluginConfigs() {
        super();
    }

    @Override
    public void loadOptions(List<ConfigOption> options) {
        super.loadOptions(options);
    }
}
