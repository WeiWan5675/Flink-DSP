package com.weiwan.dsp.console.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.entity.Plugin;
import com.weiwan.dsp.core.plugin.container.PluginClassLoader;
import com.weiwan.dsp.core.pub.JarScanner;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;

public class PluginServiceImplTest {

    @Test
    public void test() {
        File file = new File("E:\\MyCloud\\Develop\\Notes");

        BasicFileAttributes attr = null;
        try {
            Path path = file.toPath();
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建时间
        long l = attr.creationTime().toMillis();
        System.out.println(l);
    }

    @Test
    public void testFileToJson() throws IOException, ClassNotFoundException {
        File file = new File("F:\\project\\Flink-DSP\\dsp-extends\\dsp-plugin-example\\src\\main\\resources\\conf\\Dsp-Plugin-Example.json");
        String content = FileUtil.readFileContent(file);
        JSONArray pluginJsons = ObjectUtil.contentToObject(content, JSONArray.class);
//        for (int i = 0; i < pluginJsons.size(); i++) {
//            JSONObject o = (JSONObject) pluginJsons.get(i);
//            System.out.println(o);
//        }

        PluginClassLoader pluginClassLoader = new PluginClassLoader();
        for (Object pluginJson : pluginJsons) {
            JSONObject object = new JSONObject((Map<String, Object>) pluginJson);
            PluginConfig pluginConfig = ObjectUtil.mapToBean(object, PluginConfig.class);
            System.out.println(pluginConfig);
            System.out.println();
            pluginClassLoader.loadClass(pluginConfig.getPluginClass());

        }
    }

    @Test
    public void testPluginClassLoader() throws IOException, ClassNotFoundException {
        File file = new File("F:\\project\\Flink-DSP\\dsp-extends\\dsp-plugin-example\\target\\dsp-plugin-example-1.0.0.jar");
        URL url = file.toURI().toURL();
        PluginClassLoader pluginClassLoader = new PluginClassLoader();
        //加载jar文件
        pluginClassLoader.loadJarFile(url);
        //扫描jar文件
        JarScanner scanner = new JarScanner(pluginClassLoader, url);
        List<URL> metaFiles = scanner.scanFiles("Dsp-Plugin", ".json");
        InputStream inputStream = metaFiles.get(0).openStream();
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        String json = new String(buffer);
        JSONArray pluginJsons = ObjectUtil.contentToObject(json, JSONArray.class);
        for (Object pluginJson : pluginJsons) {
            JSONObject object = new JSONObject((Map<String, Object>) pluginJson);
            PluginConfig pluginConfig = ObjectUtil.mapToBean(object, PluginConfig.class);
            Class<?> aClass = pluginClassLoader.loadClass(pluginConfig.getPluginClass());
            System.out.println(aClass);
        }
    }

    @Test
    public void testPluginJarFile() {
        File file = new File("F:\\project\\Flink-DSP\\tmp\\upload\\326c7ef75594e68afd97c090e22c12d8_1645512670325.jar");
        if (!file.exists()) {
            System.out.println("文件不存在");
        } else {
            System.out.println("文件存在");
        }
    }
}