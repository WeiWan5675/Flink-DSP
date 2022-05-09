package com.weiwan.dsp.console.service;

import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.query.ConfigQuery;
import com.weiwan.dsp.console.model.vo.ConfigVo;

public interface ConfigService {
    PageWrapper<ConfigVo> searchConfig(ConfigQuery query);

    void createConfig(ConfigVo configVo);

    void updateConfig(ConfigVo configVo);

    boolean deleteConfig(Integer configId);
}
