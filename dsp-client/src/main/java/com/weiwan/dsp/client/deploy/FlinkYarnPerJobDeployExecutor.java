package com.weiwan.dsp.client.deploy;


import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.FlinkOnYarnPerConfigs;
import com.weiwan.dsp.core.deploy.JobInfo;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.security.SecurityConfiguration;
import org.apache.flink.runtime.security.SecurityUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/19 14:58
 * @ClassName: FlinkYarnPerJobDeployExecutor
 * @Description:
 **/
public class FlinkYarnPerJobDeployExecutor extends FlinkOnYarnClusterJobDeployExecutor {

    private Configuration flinkConfiguration;
    private FlinkOnYarnPerConfigs configs;
    private JobInfo jobInfo;
    private YarnClient yarnClient;
    private YarnConfiguration yarnConfiguration;

    public FlinkYarnPerJobDeployExecutor(DspConfig dspConfig) {
        super(dspConfig);
    }

    @Override
    public void init() throws Exception {
        super.init();
        this.configs = new FlinkOnYarnPerConfigs(getEngineConfigs());
        this.flinkConfiguration = getFlinkConfiguration();
        this.yarnConfiguration = getYarnConfiguration();
        this.jobInfo = getDspJob();
        this.checkpointSetting(flinkConfiguration, configs);
        this.savepointSetting(flinkConfiguration, configs);
        this.internalSetting(flinkConfiguration, configs);
        this.flinkClusterSetting(flinkConfiguration, configs);
        loginSecurityEnv();
        //初始化YarnRestClient
        this.yarnClient = YarnClient.createYarnClient();
        this.yarnClient.init(yarnConfiguration);
        this.yarnClient.start();
        List<ApplicationReport> applications =
                yarnClient.getApplications();
        System.out.println(applications);
    }


    public void loginSecurityEnv() throws Exception {
        SecurityUtils.install(new SecurityConfiguration(flinkConfiguration));
    }

    @Override
    public void start() throws Exception {
        ClusterSpecification clusterSpecification =
                new ClusterSpecification.ClusterSpecificationBuilder()
                        .setMasterMemoryMB(768)
                        .setTaskManagerMemoryMB(768)
                        .setSlotsPerTaskManager(1)
                        .createClusterSpecification();


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
    public void close() throws Exception {
        if (yarnClient != null) {
            this.yarnClient.stop();
        }
    }
}
