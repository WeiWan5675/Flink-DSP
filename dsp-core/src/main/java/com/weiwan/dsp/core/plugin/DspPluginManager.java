package com.weiwan.dsp.core.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.core.plugin.container.PluginClassLoader;
import com.weiwan.dsp.core.pub.JarScanner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xiaozhennan
 * @description: 默认的插件管理器, 从classpath进行插件的加载
 * 1. 可以获取到当前插件的全部信息
 * 2. 可以加载/卸载插件
 * 3. 可以使用PluginConfig创建一个插件
 * 4. 插件管理器管理的是Class和Dsp-Plugin-xxx.json文件和jar包的映射关系
 * 5. 不管是web还是客户端,实际上都是通过插件管理器注册DSP_HOME/plugin目录
 * 将注册的插件信息保存,根据flow.json文件中配置的className进行插件的注册
 * 这样就避免了每次要把所有jar都提交到计算集群,避免了classpath的污染。
 * <p>
 * 卸载插件的场景
 * 0. jar文件可以删除, 但是文件里的插件只能禁用
 * 1. jar卸载(直接卸载classloader 和挂载的metadataList)
 * 2. 插件class卸载(根据className找到对应的MetaData然后标记禁用)
 * 3. 更新插件(插件包完整更新 1. classloader更新 2. 插件metaData更新)
 * 4. 删除插件 (修改插件metadata的disabled)
 * 5. 添加插件(添加classloader 添加metadataList)
 * 后台管理时
 * 1. 根据插件url查找对应的记录
 * 2. 根据url能够得到所有的className
 * 3. 根据className禁用对应的metaData
 * 4. 根据url卸载所有的插件
 * classLoad = url
 * url 对多个 className
 * className 对url
 */
public class DspPluginManager {
    private static final Logger logger = LoggerFactory.getLogger(DspPluginManager.class);
    private PluginRepoV2 pluginRepo;
    private ClassLoader contextClassLoader;
    private Map<String, PluginClassLoader> classLoaderMap = new ConcurrentHashMap<>();
    private String baseDir;
    private static DspPluginManager pluginManager;

    private DspPluginManager() {
        this.pluginRepo = new PluginRepoV2();
        this.contextClassLoader = Thread.currentThread().getContextClassLoader();
    }


    private DspPluginManager(String baseDir) {
        this();
        this.baseDir = baseDir;
        if (baseDir != null) {
            this.registerPlugins(new File(baseDir));
        }
    }




    public static DspPluginManager getInstance() {
        if (pluginManager == null) {
            synchronized (DspPluginManager.class) {
                if (pluginManager == null) {
                    logger.info("Initialize the Dsp Plugin Manager");
                    pluginManager = new DspPluginManager();
                    pluginManager.init();
                }
            }
        }
        return pluginManager;
    }


    private void init() {

    }


    public void load(URL jarUrl) throws Exception {
        CheckTool.checkArgument(jarUrl != null);


        //保存插件加载的classload,方便卸载插件
        PluginClassLoader pluginClassLoader = classLoaderMap.get(jarUrl.toString());
        if (pluginClassLoader == null) {
            pluginClassLoader = new PluginClassLoader();
            this.classLoaderMap.put(jarUrl.toString(), pluginClassLoader);
        }
        //加载jar文件
        pluginClassLoader.loadJarFile(jarUrl);

        //扫描jar文件
        JarScanner scanner = new JarScanner(pluginClassLoader, jarUrl);
        List<URL> metaFiles = scanner.scanFiles("Dsp-Plugin", ".json");

        //解析插件定义元信息文件
        List<PluginMetaData> pluginMetaDatas = analyzeMetadata(jarUrl, metaFiles);

        //如果插件校验失败, 就需要把这个插件jar卸载掉
        //一个插件jar完全校验通过后,这个集合就是包含check完成的插件
        Map<String, PluginMetaData> checkedInfos = new HashMap<>();
        try {
            for (PluginMetaData pluginMetaData : pluginMetaDatas) {
                if (checkPluginMetaData(pluginMetaData)) {
                    pluginMetaData.setCheckPassed(true);
//                    pluginMetaData.setPluginClazz((Class<? extends Plugin>) pluginClassLoader.loadClass(pluginMetaData.getPluginClass()));
                    checkedInfos.put(pluginMetaData.getPluginClass(), pluginMetaData);
                } else {
                    if(logger.isDebugEnabled()){
                        logger.debug("The plugin loading verification fails. Skip the plugin. Plugin path: {}, plugin Name: {}, plugin class: {}", pluginMetaData.getPluginUrl(), pluginMetaData.getPluginName(), pluginMetaData.getPluginClass());
                    }
                }
            }
        } catch (Exception e) {
            //检验过程中出现异常,就把插件卸载并且打印error
            logger.error("The plugin jar cannot pass the verification. Please check and reload. Plugin URL: {}", jarUrl, e);
            throw e;
        }

        pluginRepo.save(checkedInfos);
    }


    private boolean checkPluginMetaData(PluginMetaData metaInfo) {
        if (metaInfo == null) {
            return false;
        }
        CheckTool.checkState(StringUtils.isNotBlank(metaInfo.getPluginClass()), "Plugin class type cannot be empty, name: {}", metaInfo.getPluginName());
        CheckTool.checkState(StringUtils.isNotBlank(metaInfo.getPluginName()), "Plugin name cannot be empty, className: {}", metaInfo.getPluginClass());
        CheckTool.checkState(metaInfo.getPluginType() != null, "Plugin type cannot be empty, className: {}", metaInfo.getPluginClass());

        PluginClassLoader pluginClassLoader = classLoaderMap.get(metaInfo.getPluginUrl().toString());
        if (pluginClassLoader == null) {
            //找不到插件的classLoader 属于已经卸载的
            CheckTool.checkState(false, "The loader of the current plug-in cannot be found. Please check whether the current plugin has been removed");
        }

        try {
            Class<?> aClass = pluginClassLoader.loadClass(metaInfo.getPluginClass());
            if (aClass == null) return false;
        } catch (ClassNotFoundException e) {
            //找不到就返回false
            logger.warn("The plugin could not be found, className: {}", metaInfo.getPluginClass());
            return false;
        }

        return true;
    }


    private List<PluginMetaData> analyzeMetadata(URL jarUrl, List<URL> files) throws IOException {
        //可能存在多个定义文件,这时候也都是统一解析成一个 一个jar文件  可能对应多个 DSP-Plugin-xxx.json

        List<PluginMetaData> list = new ArrayList<>();
        for (URL jsonUrl : files) {
            List<PluginMetaData> pluginMetaInfos = loadAndAnalyze(jarUrl, jsonUrl);
            if (pluginMetaInfos != null) {
                list.addAll(pluginMetaInfos);
            }
        }
        return list;
    }

    private List<PluginMetaData> loadAndAnalyze(URL jarUrl, URL jsonUrl) throws IOException {
        List<PluginMetaData> metaInfos = new ArrayList<>();
        InputStream inputStream = jsonUrl.openStream();
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        String json = new String(buffer);
        JSONArray jsonArray = JSON.parseArray(json);
        List<JSONObject> jsonObjects = jsonArray.toJavaList(JSONObject.class);
        for (JSONObject jsonObject : jsonObjects) {
            PluginMetaData metaData = new PluginMetaData(jsonObject, jarUrl);
            metaData.setLoadTime(System.currentTimeMillis());
            metaData.setLastReloadTime(metaData.getLastReloadTime());
            metaInfos.add(metaData);
        }
        return metaInfos;
    }


    public void unload(URL jarUrl) {
        CheckTool.checkArgument(jarUrl != null);

        Map<String, PluginMetaData> delete = pluginRepo.delete(jarUrl.toString());
        if (delete != null) {
            logger.info("The plugin metadata is deleted from the plugin repository, and the following plugin metadata is deleted:");
            delete.keySet().forEach(s -> logger.info("deleted: " + s));
        }
        if (classLoaderMap.containsKey(jarUrl.toString())) {
            PluginClassLoader pluginClassLoader = classLoaderMap.get(jarUrl.toString());
            pluginClassLoader.unloadJarFiles();
            classLoaderMap.remove(jarUrl.toString());
            logger.info("卸载插件jar文件, jar文件已经释放. jarFile: {}", jarUrl.toString());
        }
    }


    public void registerPlugins(File... files) {
        List<URL> urlList = findJars(files);
        for (URL jarUrl : urlList) {
            try {
                load(jarUrl);
            } catch (Exception e) {
                logger.info("Plugin registration failed, please check the log for details", e);
                throw new RuntimeException(e);
            }
        }


    }

    private List<URL> findJars(File[] files) {
        //这里需要把路径下所有的*.jar都获取到
        CheckTool.checkState(files != null);
        //判断是文件还是文件夹, 如果是文件夹  就递归获取, 如果是文件就判断是否是jar结尾
        List<URL> urlList = getJars(files);
        return urlList;
    }


    public void unregisterPlugins(File... files) {
        List<URL> jars = findJars(files);
        for (URL jarUrl : jars) {
            unload(jarUrl);
        }
    }

    /**
     * 递归获得所有插件jar
     *
     * @param files 需要获取的文件地址,可以是文件夹或者直接是插件jar
     * @return 插件jars列表
     */
    private List<URL> getJars(File[] files) {
        List<URL> urlList = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                List<URL> jars = getJars(file.listFiles());
                urlList.addAll(jars);
            } else {
                if (FileUtil.checkFileSuffix(file.getAbsolutePath(), ".jar")) {
                    try {
                        if (FileUtil.existsFile(file)) {
                            URL url = file.toURI().toURL();
                            urlList.add(url);
                        }
                    } catch (MalformedURLException e) {
                        logger.warn("Cannot find plugin file, name: {}, skipping the file", file.getName());
                    }
                }
            }
        }
        return urlList;
    }

    public PluginRepoV2 getPluginRepo() {
        return pluginRepo;
    }

    public void setPluginRepo(PluginRepoV2 pluginRepo) {
        this.pluginRepo = pluginRepo;
    }



    public <T extends Plugin> Class<T> loadPluginClass(PluginMetaData metaData) throws ClassNotFoundException {
        if (metaData == null) {
            return null;
        } else {
            URL pluginUrl = metaData.getPluginUrl();
            PluginClassLoader pluginClassLoader = classLoaderMap.get(pluginUrl.toString());
            if (pluginClassLoader != null) {
                return (Class<T>) pluginClassLoader.loadClass(metaData.getPluginClass());
            }
        }
        return null;
    }

    public boolean checkLoad(String className) {
        PluginMetaData metaData = pluginRepo.search(className);
        if(metaData != null){
            try {
                Class<Plugin> clazz = loadPluginClass(metaData);
                if(clazz == null) return false;
                Plugin plugin = clazz.newInstance();
                if(plugin != null) return true;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                return false;
            }
        }
        return false;
    }
}
