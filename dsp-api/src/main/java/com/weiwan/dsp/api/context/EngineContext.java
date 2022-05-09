package com.weiwan.dsp.api.context;

import com.weiwan.dsp.api.config.core.*;
import com.weiwan.dsp.api.config.flow.FlowConfig;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.metrics.MetricManager;
import com.weiwan.dsp.api.resolve.UnresolvedCollector;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 13:10
 * @description:
 */
public interface EngineContext extends Serializable {

    /**
     * 获取流程配置
     * @return
     */
    FlowConfig getFlowConfig();

    /**
     * 获取节点配置
     * @param nodeId 需要指定一个节点ID
     * @return
     */
    NodeConfig getNodeConfig(String nodeId);

    /**
     * 获取核心配置
     * @return
     */
    CoreConfig getCoreConfig();

    /**
     * 获取EngineConfig
     * @return
     */
    EngineConfig getEngineConfig();

    /**
     * 获取Metric配置
     * @return
     */
    MetricConfig getMetricConfig();

    /**
     * 获取全局得未解析收集配置
     * @return
     */
    UnresolvedCollectorConfig getUnresolvedCollectorConfig();


    /**
     * 获取全局限速器配置
     * @return
     */
    SpeedLimiterConfig getSpeedLimiterConfig();

    /**
     * 获取作业配置
     * @return
     */
    JobConfig getJobConfig();

    /**
     * 获取作业名称
     * @return
     */
    String getJobName();

    /**
     * 获取作业ID
     * @return
     */
    String getJobId();

    /**
     * 获取用户自定义配置
     * @return
     */
    CustomConfig getCustomConfig();

    /**
     * 获取节点MetricManager
     * @param nodeId
     * @return
     */
    MetricManager getMetricManager(String nodeId);

    /**
     *
     * @param nodeId
     * @return
     */
    UnresolvedCollector getUnresolvedCollector(String nodeId);


}
