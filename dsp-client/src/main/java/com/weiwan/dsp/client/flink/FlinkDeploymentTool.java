package com.weiwan.dsp.client.flink;

/**
 * @author: xiaozhennan
 * @date: 2021/6/3 14:33
 * @description:
 */
public interface FlinkDeploymentTool {


    public void submitJob(JobInfo jobInfo);

    public void stopJob(JobInfo jobInfo);
}
