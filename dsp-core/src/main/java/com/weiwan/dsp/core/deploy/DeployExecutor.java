package com.weiwan.dsp.core.deploy;

import com.weiwan.dsp.api.config.core.Configs;
import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.DspContextConfig;
import com.weiwan.dsp.api.enums.ApplicationState;
import org.apache.flink.client.cli.CliArgsException;

import java.util.concurrent.Callable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/30 14:51
 * @description:
 */
public interface DeployExecutor {

    void initDeploy() throws Exception;

    void closeDeploy() throws Exception;

    void start() throws Exception;

    void stop() throws Exception;

    void cancel() throws Exception;

    void refresh() throws Exception;

    ApplicationState getApplicationState();

    void upgradeDeploy(DspConfig dspContext);
}
