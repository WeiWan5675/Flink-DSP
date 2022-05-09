package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.common.config.ConfigOption;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/19 17:06
 * @ClassName: FlinkStandaloneConfigs
 * @Description: StandaloneConfigs
 **/
public class FlinkStandaloneConfigs extends FlinkConfigs {
    public FlinkStandaloneConfigs(Configs configs) {
        super(configs);
    }

    @Override
    public void loadOptions(List<ConfigOption> options) {
        super.loadOptions(options);
    }
}
