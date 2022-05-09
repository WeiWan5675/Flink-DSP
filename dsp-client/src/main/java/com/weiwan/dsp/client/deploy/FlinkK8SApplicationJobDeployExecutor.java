package com.weiwan.dsp.client.deploy;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/19 15:00
 * @ClassName: FlinkK8SApplicationJobDeployExecutor
 * @Description:
 **/
public class FlinkK8SApplicationJobDeployExecutor extends JobDeployExecutor {


    public FlinkK8SApplicationJobDeployExecutor(DspConfig dspConfig) {
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
    public void init() {

    }

    @Override
    public void close() throws Exception {

    }
}