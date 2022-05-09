package com.weiwan.dsp.core.resolve;

import cn.hutool.core.util.TypeUtil;
import com.weiwan.dsp.api.config.core.CollectorHandlerConfigs;
import com.weiwan.dsp.api.config.core.UnresolvedCollectorConfig;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.enums.UnresolvedHandlerType;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.common.config.AbstractConfig;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.common.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author: xiaozhennan
 * @date: 2021/6/21 18:38
 * @description:
 */
public class UnresolvedDataCollectorFactory {

    private static final Logger _LOGGER = LoggerFactory.getLogger(UnresolvedDataCollectorFactory.class);

    public static UnresolvedDataCollector createCollectory(final UnresolvedCollectorConfig config, String nodeId, String nodeName, NodeType nodeType, String jobId, String jobName) {
        long maxSamplingRecord = config.getMaxSamplingRecord();
        long samplingInterval = config.getSamplingInterval();
        UnresolvedHandlerType collectorHandler = config.getCollectorHandler();
        DspUnresolvedDataCollector collector = null;
        Class<?> aClass = null;
        try {
            aClass = Class.forName(collectorHandler.getHandlerClass());
            collector = (DspUnresolvedDataCollector) aClass.newInstance();
            ReflectUtil.setFieldValue(DspUnresolvedDataCollector.class, collector, "maxSamplingRecord", maxSamplingRecord);
            ReflectUtil.setFieldValue(DspUnresolvedDataCollector.class, collector, "samplingInterval", samplingInterval);
            ReflectUtil.setFieldValue(DspUnresolvedDataCollector.class, collector, "nodeId", nodeId);
            ReflectUtil.setFieldValue(DspUnresolvedDataCollector.class, collector, "nodeName", nodeName);
            ReflectUtil.setFieldValue(DspUnresolvedDataCollector.class, collector, "nodeType", nodeType);
            ReflectUtil.setFieldValue(DspUnresolvedDataCollector.class, collector, "jobName", jobName);
            ReflectUtil.setFieldValue(DspUnresolvedDataCollector.class, collector, "jobId", jobId);
        } catch (ClassNotFoundException e) {
            _LOGGER.error("Unable to find corresponding unresolved processor class", e);
        } catch (IllegalAccessException e) {
            _LOGGER.error("The corresponding unresolved processor class constructor cannot be found", e);
        } catch (InstantiationException e) {
            _LOGGER.error("Exception creating unresolved processor", e);
        }

        List<Method> publicMethods = cn.hutool.core.util.ReflectUtil.getPublicMethods(aClass, "stop", "handle");
        List<Method> initMethods = publicMethods.stream().filter(new Predicate<Method>() {
            @Override
            public boolean test(Method method) {
                return "init".equals(method.getName()) && method.getParameterTypes()[0].getName() != CollectorHandlerConfigs.class.getName();
            }
        }).collect(Collectors.toList());
        Method initMethod = null;
        Class<?> paramClass = null;
        if(initMethods != null && initMethods.size() > 0){
            initMethod = initMethods.get(0);
            paramClass = TypeUtil.getParamClass(initMethod, 0);
        }
        if(paramClass == null) paramClass = CollectorHandlerConfigs.class;
        Constructor<?> constructor = cn.hutool.core.util.ReflectUtil.getConstructor(paramClass);
        CheckTool.checkNotNull(constructor == null, "The Unresolved configuration class must provide a constructor whose parameter is Map");
        CollectorHandlerConfigs newUnresolvedConfigs = null;
        try {
            newUnresolvedConfigs = (CollectorHandlerConfigs) constructor.newInstance();
        } catch (Exception e) {
            _LOGGER.error("无法创建UnresolvedCollector", e);
            throw new DspException(e);
        }
        newUnresolvedConfigs.putAll(config.getHandlerConfigs());
        config.setHandlerConfigs(newUnresolvedConfigs);
        if (collector != null) {
            collector.open(config);
        }
        return collector;
    }


}
