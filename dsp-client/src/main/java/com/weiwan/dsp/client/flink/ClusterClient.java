package com.weiwan.dsp.client.flink;

import org.apache.hadoop.mapreduce.Job;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/8/3 21:55
 * @Package: com.weiwan.dsp.client.flink
 * @ClassName: ClusterClient
 * @Description: 集群客户端工具
 **/
public interface ClusterClient {
    boolean isAHealthyCluster();

    DspJob getJob(String jobId);

    boolean stopJob(DspJob dspJob);

    boolean cancelJob(DspJob dspJob);

    List<DspJob> getJobs(String status);

    boolean submitJob(DspJob dspJob);

}
