package com.weiwan.dsp.plugins.split;

import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.core.plugin.RichSplitPlugin;
import com.weiwan.dsp.plugins.config.CopyStreamSplitPluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: xiaozhennan
 * @date: 2021/6/22 9:37
 * @description: 一个单纯的复制流功能, 会把所有收到的数据匹配
 */
public class CopyStreamSplitPlugin extends RichSplitPlugin<CopyStreamSplitPluginConfig> {

    private static final Logger logger = LoggerFactory.getLogger(CopyStreamSplitPlugin.class);

    @Override
    public boolean match(DataRecord record) {
        logger.debug("Copy stream split , match all by default true");
        return true;
    }

    @Override
    public void stop() {
        logger.debug("Copy stream split plugin stop function running.");
    }

    @Override
    public void init(CopyStreamSplitPluginConfig config) {
        logger.debug("Copy stream split plugin init function running.");
    }
}
