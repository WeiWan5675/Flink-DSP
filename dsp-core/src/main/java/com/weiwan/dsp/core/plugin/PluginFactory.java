package com.weiwan.dsp.core.plugin;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.enums.PluginType;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.core.plugin.container.PluginClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author: xiaozhennan
 * @description:
 */
public class PluginFactory {

    private static final Logger logger = LoggerFactory.getLogger(PluginFactory.class);
    private static PluginFactory instance;
    private volatile boolean inited;
    private DspPluginManager pluginManager;
    private PluginRepo pluginRepo;
    private PluginClassLoader pluginClassLoader;

    private PluginFactory() {
    }

    public static PluginFactory getInstance() {
        if (instance == null) {
            synchronized (PluginFactory.class) {
                if (instance == null) {
                    instance = new PluginFactory();
                    instance.init();
                    instance.inited = true;
                }
            }
        }
        return instance;
    }


    private void init() {
        this.pluginManager = DspPluginManager.getInstance();
        this.pluginRepo = pluginManager.getPluginRepo();
        this.pluginClassLoader = new PluginClassLoader();
    }


    public Plugin createPlugin(PluginConfig config) {
        if (checkPlugin(config)) {
            Class<Plugin> clazz = null;
            try {
                PluginMetaData metaData = pluginRepo.search(config.getPluginClass());
                if (metaData != null) {
                    clazz = pluginManager.loadPluginClass(metaData);
                } else {
                    clazz = (Class<Plugin>) pluginClassLoader.loadClass(config.getPluginClass());
                }
            } catch (ClassNotFoundException e) {
                logger.error("无法从classpath或者插件管理器中找到插件: {}", config.getPluginClass());
                throw new RuntimeException(e);
            }
            try {
                if (!CheckTool.checkIsNull(clazz, "Unable to find class for plugin")) {
                    return newInstancePlugin(config, clazz);
                }
            } catch (Exception e) {
                logger.error("Failed to create plugin", e);
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    private boolean checkPlugin(PluginConfig pluginConfig) {
        PluginType pluginType = null;
        try {
            pluginType = pluginConfig.getPluginType();
        } catch (Exception e) {
            logger.error("无法获取到插件的类型,请检查配置文件");
            throw new RuntimeException(String.format("插件: %s, 插件类型错误或者未指定", pluginConfig.getPluginName()));
        }
        if (pluginType == PluginType.SYSTEM) {
            //系统插件不处理
            return false;
        }
        //这里是三个属性
        if (!PluginConfig.PLUGIN_CLASS.check(pluginConfig.getPluginClass()) && pluginConfig.getPluginType() != PluginType.SYSTEM) {
            throw new RuntimeException(String.format("插件: %s, 必须指定一个插件的ClassName", pluginConfig.getPluginName()));
        }
        return true;
    }


    private Plugin newInstancePlugin(PluginConfig pluginConfig, Class<? extends Plugin> aClass) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Plugin plugin = aClass.newInstance();
        List<Method> publicMethods = ReflectUtil.getPublicMethods(aClass, "stop", "process", "read", "batchRead", "readEnd", "write", "batchWrite", "match", "handle");
        Method initMethod = publicMethods.stream().filter(new Predicate<Method>() {
            @Override
            public boolean test(Method method) {
                return "init".equals(method.getName()) && method.getParameterTypes()[0].getName() != PluginConfig.class.getName();
            }
        }).collect(Collectors.toList()).get(0);
        Class<?> paramClass = TypeUtil.getParamClass(initMethod, 0);
        Constructor<?> constructor = ReflectUtil.getConstructor(paramClass);
        CheckTool.checkNotNull(constructor == null, "The plug-n configuration class must provide a constructor whose parameter is Map");
        PluginConfig newPluginConfig = (PluginConfig) constructor.newInstance();
        PluginConfig finalPluginConfig = ObjectUtil.mergeObjects(newPluginConfig, pluginConfig);
        plugin.setPluginConfig(finalPluginConfig);
        return plugin;
    }
}
