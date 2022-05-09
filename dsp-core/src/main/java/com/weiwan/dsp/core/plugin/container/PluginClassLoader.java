package com.weiwan.dsp.core.plugin.container;


import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

/**
 * @author: xiaozhennan
 * @description:
 */
public class PluginClassLoader extends URLClassLoader {

    private Map<String, JarURLConnection> cachedJarFiles = new ConcurrentHashMap<>();

    public PluginClassLoader(URL... jarUrl) {
        super(new URL[]{}, findParentClassLoader());
    }

    /**
     * 将指定的文件url添加到类加载器的classpath中去，并缓存jar connection，方便以后卸载jar
     *
     * @param
     */
    public void loadJarFile(URL file) {
        URL url = null;
        try {
            url = fixJarUrl(file);
            // 打开并缓存文件url连接
            URLConnection uc = url.openConnection();
            uc.connect();
            if (uc instanceof JarURLConnection) {
//                uc.setUseCaches(true);
//                ((JarURLConnection) uc).getManifest();
                cachedJarFiles.put(url.toString(), (JarURLConnection) uc);
            }
        } catch (IOException e) {
            System.err.println("Failed to cache plugin JAR file: " + url.toExternalForm());
        }
        addURL(url);
    }

    /**
     * 用于将url转换成jarURl
     * @param oldUrl 需要处理的URL
     * @return 处理好的jarURL
     * @throws IOException url非法时抛出异常
     */
    public URL fixJarUrl(URL oldUrl) throws IOException{
        URL url = null;
        if (oldUrl.toString().startsWith("file")) {
            url = new URL("jar:" + oldUrl.toString() + "!/");
        } else if (oldUrl.toString().startsWith("http")) {
            url = new URL("jar:" + oldUrl.toString() + "!/");
        } else {
            url = oldUrl;
        }
        return url;
    }


    /**
     * 卸载jar包
     */
    public void unloadJarFiles() {
        for (String urlKey : cachedJarFiles.keySet()) {
            try {
                JarURLConnection jarURLConnection = cachedJarFiles.get(urlKey);
                if (jarURLConnection != null) jarURLConnection.getJarFile().close();
                jarURLConnection = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void unloadJarFile(URL file) {
        if (file == null) return;
        URL url = null;
        try {
            url = fixJarUrl(file);
            JarURLConnection jarURLConnection = cachedJarFiles.get(url.toString());
            if (jarURLConnection != null) {
                jarURLConnection.getJarFile().close();
                cachedJarFiles.remove(url.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定位基于当前上下文的父类加载器
     *
     * @return 返回可用的父类加载器.
     */
    private static ClassLoader findParentClassLoader() {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = PluginClassLoader.class.getClassLoader();
        }
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        return parent;
    }
}
