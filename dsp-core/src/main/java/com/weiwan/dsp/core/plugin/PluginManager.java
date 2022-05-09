//package com.weiwan.dsp.core.plugin;
//
//import cn.hutool.core.util.ReflectUtil;
//import cn.hutool.core.util.TypeUtil;
//import com.weiwan.dsp.api.plugin.PluginConfig;
//import com.weiwan.dsp.api.enums.PluginType;
//import com.weiwan.dsp.api.plugin.Plugin;
//import com.weiwan.dsp.common.config.AbstractConfig;
//import com.weiwan.dsp.common.utils.CheckTool;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Method;
//import java.util.*;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
///**
// * @author: xiaozhennan
// * @date: 2021/6/15 10:22
// * @description:
// */
//public class PluginManager {
//
//    private static PluginRepo pluginRepo = new PluginRepo();
//    private static final Logger logger = LoggerFactory.getLogger(PluginManager.class);
//    private static volatile PluginManager pluginManager;
//
//    public static PluginManager getInstance() {
//        if (pluginManager == null) {
//            synchronized (PluginManager.class) {
//                if (pluginManager == null) {
//                    logger.info("Initialize the Dsp Plugin manager");
//                    pluginManager = new PluginManager();
//                    pluginManager.init();
//                }
//            }
//        }
//        return pluginManager;
//    }
//
//    private void init() {
//    }
//
//
//    private PluginManager() {
//    }
//
//    public void registerPlugins(Map<String, PluginConfig> plugins) throws ClassNotFoundException {
//        //注册所有插件
//        if (plugins == null && plugins.size() == 0) {
//            return;
//        }
//        for (String pluginName : plugins.keySet()) {
//            PluginConfig pluginConfig = plugins.get(pluginName);
//            registerPlugin(pluginConfig);
//        }
//    }
//
//    public Plugin createPlugin(PluginConfig pluginConfig) throws Exception {
//        logger.info("create plugin: {}", pluginConfig.getPluginName());
//        try {
//            if (pluginConfig != null) {
//                if (checkPlugin(pluginConfig)) {
//                    Class<? extends Plugin> aClass = pluginRepo.get(pluginConfig.getPluginClass());
//                    Plugin plugin = aClass.newInstance();
//                    List<Method> publicMethods = ReflectUtil.getPublicMethods(aClass, "stop", "process", "read", "batchRead", "readEnd", "write", "batchWrite", "match", "handle");
//                    Method initMethod = publicMethods.stream().filter(new Predicate<Method>() {
//                        @Override
//                        public boolean test(Method method) {
//                            return "init".equals(method.getName()) && method.getParameterTypes()[0].getName() != PluginConfig.class.getName();
//                        }
//                    }).collect(Collectors.toList()).get(0);
//                    Class<?> paramClass = TypeUtil.getParamClass(initMethod, 0);
//                    Constructor<?> constructor = ReflectUtil.getConstructor(paramClass);
//                    CheckTool.checkNotNull(constructor == null, "The plug-n configuration class must provide a constructor whose parameter is Map");
//                    PluginConfig newPluginConfig = (PluginConfig) constructor.newInstance();
//                    newPluginConfig.putAll(pluginConfig);
//                    plugin.setPluginConfig(newPluginConfig);
//                    return plugin;
//                }
//            }
//        } catch (Exception e) {
//            logger.error("An error occurred while creating the plugin, plugin name:{}", pluginConfig.getPluginName(), e);
//            throw new RuntimeException(e);
//        }
//        return null;
//    }
//
//
//    public void registerPlugin(PluginConfig pluginConfig) throws ClassNotFoundException {
//        String pluginClass = pluginConfig.getPluginClass();
//        if (checkPlugin(pluginConfig)) {
//            Class<? extends Plugin> clazz = pluginRepo.get(pluginClass);
//            if (clazz == null) {
//                Class<?> aClass = Class.forName(pluginClass, false, Thread.currentThread().getContextClassLoader());
//                pluginRepo.addPlugin((Class<? extends Plugin>) aClass);
//            }
//        }
//    }
//
//    private boolean checkPlugin(PluginConfig pluginConfig) {
//        PluginType pluginType = null;
//        try {
//            pluginType = pluginConfig.getPluginType();
//        } catch (Exception e) {
//            logger.error("无法获取到插件的类型,请检查配置文件");
//            throw new RuntimeException(String.format("插件: %s, 插件类型错误或者未指定", pluginConfig.getPluginName()));
//        }
//        if (pluginType == PluginType.SYSTEM) {
//            //系统插件不处理
//            return false;
//        }
//        //这里是三个属性
//        if (pluginConfig.getPluginClass().equals(PluginConfig.PLUGIN_CLASS.defaultVar()) && pluginConfig.getPluginType() != PluginType.SYSTEM) {
//            throw new RuntimeException(String.format("插件: %s, 必须指定一个插件的ClassName", pluginConfig.getPluginName()));
//        }
//        return true;
//    }
//
//    public Set<Plugin> createPlugins(Map<String, PluginConfig> plugins) throws Exception {
//        Set<Plugin> tmpSet = new LinkedHashSet<>();
//        for (String key : plugins.keySet()) {
//            tmpSet.add(createPlugin(plugins.get(key)));
//        }
//        return tmpSet;
//    }
//}
