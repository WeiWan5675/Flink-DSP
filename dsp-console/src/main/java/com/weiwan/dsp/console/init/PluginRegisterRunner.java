package com.weiwan.dsp.console.init;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.MD5Utils;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.entity.Plugin;
import com.weiwan.dsp.console.model.entity.PluginJar;
import com.weiwan.dsp.console.service.PluginService;
import com.weiwan.dsp.core.plugin.container.PluginClassLoader;
import com.weiwan.dsp.core.pub.JarScanner;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/4/4 16:54
 * @description
 */
@Component
public class PluginRegisterRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PluginRegisterRunner.class);

    @Autowired
    private PluginService pluginService;

    @Override
    public void run(String... args) throws Exception {
        //查询数据库, 查出所有origin为system的jar记录, 以及所有pluginClass记录
        Map<String, PluginJar> pluginJarNameMap = pluginService.findPluginJarNameMap();
        Map<String, Plugin> pluginClassMap = pluginService.findPluginClassMap();
        //定义JarMd5Map, 用于查找jar文件的md5是否在数据库中已存在
        Map<String, PluginJar> pluginJarMd5Map = new HashMap<>();
        Collection<PluginJar> pluginJars = pluginJarNameMap.values();
        pluginJars.forEach(p -> pluginJarMd5Map.put(p.getPluginJarMd5(), p));
        //检查数据库中jar包的url指向的文件是否存在, 不存在就报错
        for (PluginJar pluginJar : pluginJarNameMap.values()) {
            File file = new File(new URI(pluginJar.getPluginJarUrl()));
            if (!file.exists()) {
                logger.error("System plugin jar is not found, plugin jar url: {}", file.getPath());
            }
        }
        //读取目录下的所有Pluginjar包
        String systemPluginDir = SystemEnvManager.getInstance().getSystemPluginDir();
        File[] jarFiles = new File(systemPluginDir).listFiles(name -> {
            if (name.getName().endsWith(".jar")) {
                return true;
            }
            return false;
        });
        //classLoader用于解析jar文件中的plugin
        final PluginClassLoader pluginClassLoader = new PluginClassLoader();
        //准备PluginList和JarList, 用于批量添加新插件和包
        List<PluginJar> newPluginJars = new ArrayList<>();
        List<Plugin> newPlugins = new ArrayList<>();
        for (File jarFile : jarFiles) {
            String jarName = jarFile.getName();
            //解析检查pluginClass是否存在于plugin表, 如果不存在就添加, 如果存在就跳过
            URL jarUrl = jarFile.toURI().toURL();
            try {
                //检查jarName和jarMd5是否存在于jarList, 如果不存在, 就解析, 如果存在就跳过
                String jarId = MD5Utils.md5(jarName);
                String jarMd5;
                try(FileInputStream fileInputStream = new FileInputStream(jarFile)) {
                    jarMd5 = DigestUtils.md5Hex(fileInputStream);
                }
                if (pluginJarNameMap.get(jarName) != null || pluginJarMd5Map.get(jarMd5) != null) {
                    continue;
                }
                //加载jar包并解析
                pluginClassLoader.loadJarFile(jarUrl);
                JarScanner scanner = new JarScanner(pluginClassLoader, jarUrl);
                List<URL> metaFiles = scanner.scanFiles("Dsp-Plugin", ".json");
                if (metaFiles.size() > 0) {
                    File defFile = new File(String.valueOf(metaFiles.get(0)));
                    String pluginDefFileContent = FileUtil.readFileContentFromJar(metaFiles.get(0));
                    JSONArray pluginJsons = ObjectUtil.contentToObject(pluginDefFileContent, JSONArray.class);
                    //组装PluginJar, 添加到list中待入库
                    PluginJar pluginJar = new PluginJar();
                    pluginJar.setPluginJarId(jarId);
                    pluginJar.setPluginJarName(jarName);
                    pluginJar.setPluginJarAlias("");
                    pluginJar.setRemarkMsg("");
                    pluginJar.setPluginJarMd5(jarMd5);
                    pluginJar.setPluginJarOrigin(1);
                    pluginJar.setPluginJarUrl(String.valueOf(jarUrl));
                    pluginJar.setPluginDefFile(defFile.getName());
                    pluginJar.setPluginDefFileContent(pluginDefFileContent);
                    pluginJar.setDisableMark(0);
                    pluginJar.setFileSize(jarFile.length());
                    pluginJar.setUploadTime(new Date());
                    pluginJar.setCreateUser("System");
                    pluginJar.setUpdateTime(new Date());
                    newPluginJars.add(pluginJar);
                    for (Object pluginJson : pluginJsons) {
                        JSONObject object = new JSONObject((Map<String, Object>) pluginJson);
                        PluginConfig pluginConfig = ObjectUtil.mapToBean(object, PluginConfig.class);
                        if (pluginClassMap.get(pluginConfig.getPluginClass()) == null) {
                            //pluginClass不存在, 添加到list中待入库
                            Plugin plugin = new Plugin();
                            String pluginName = pluginConfig.getPluginName();
                            String pluginClass = pluginConfig.getPluginClass();
                            String pluginId = MD5Utils.md5(String.format(ConsoleConstants.PLUGIN_ID_MD5_FORMAT, pluginName, pluginClass));
                            plugin.setPluginJarId(jarId);
                            plugin.setPluginId(pluginId);
                            plugin.setPluginName(pluginName);
                            plugin.setPluginAlias("");
                            plugin.setPluginClass(pluginClass);
                            plugin.setPluginType(pluginConfig.getPluginType().getCode());
                            plugin.setPluginDescription(pluginConfig.getPluginDescription());
                            plugin.setPluginConfigs(ObjectUtil.serialize(pluginConfig.getPluginConfigs()));
                            plugin.setPluginInfos(ObjectUtil.serialize(pluginConfig.getPluginInfos()));
                            plugin.setUpdateTime(new Date());
                            newPlugins.add(plugin);
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("plugin jar load failed, plugin jar name: {}", jarName);
                continue;
            }finally {
                pluginClassLoader.unloadJarFile(jarUrl);
            }
        }
        if(newPluginJars.size() > 0 || newPlugins.size() > 0) {
            pluginService.createJarsAndPlugins(newPluginJars, newPlugins);
        }
        try {
            pluginClassLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
