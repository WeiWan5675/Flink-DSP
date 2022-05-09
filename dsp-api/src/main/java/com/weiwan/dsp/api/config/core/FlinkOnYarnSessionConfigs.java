package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.common.config.ConfigOption;

import java.util.List;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/15 19:03
 * @Package: com.weiwan.dsp.api.config.core
 * @ClassName: FlinkOnYarnSessionConfigs
 * @Description: OnYarnSession
 **/
public class FlinkOnYarnSessionConfigs extends FlinkOnYarnConfigs {

    @Override
    public void loadOptions(List<ConfigOption> options) {
        super.loadOptions(options);
    }

    public FlinkOnYarnSessionConfigs(Map<String, Object> configs) {
        super(configs);
    }

}
