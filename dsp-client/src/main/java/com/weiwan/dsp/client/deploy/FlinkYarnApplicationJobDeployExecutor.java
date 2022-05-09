package com.weiwan.dsp.client.deploy;

import com.weiwan.dsp.api.config.core.DspConfig;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/19 14:59
 * @ClassName: FlinkYarnApplicationJobDeployExecutor
 * @Description:
 **/
public class FlinkYarnApplicationJobDeployExecutor extends FlinkOnYarnClusterJobDeployExecutor {

    public FlinkYarnApplicationJobDeployExecutor(DspConfig dspConfig) {
        super(dspConfig);
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public void cancel() throws Exception {

    }

    @Override
    public void refresh() throws Exception {

    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void close() throws Exception {

    }
}
