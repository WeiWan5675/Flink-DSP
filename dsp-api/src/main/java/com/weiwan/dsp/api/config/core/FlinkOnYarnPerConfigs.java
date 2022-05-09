package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.api.enums.ResolveOrder;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public class FlinkOnYarnPerConfigs extends FlinkOnYarnClusterConfigs {


    public FlinkOnYarnPerConfigs(Map<String, Object> configs) {
        super(configs);
    }


    @Override
    public void loadOptions(List<ConfigOption> options) {
        super.loadOptions(options);
    }
}
