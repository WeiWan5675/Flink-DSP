package com.weiwan.dsp.client.deploy;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/30 14:52
 * @description:
 */
public class LocalJobDeployExecutor extends JobDeployExecutor {



    public LocalJobDeployExecutor(DspConfig dspConfig) {
        super(dspConfig);
    }


    @Override
    public void init() throws Exception {

    }

    @Override
    public void close() throws Exception {

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
}
