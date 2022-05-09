package com.weiwan.dsp.core.pub;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @Author: xiaozhennan
 * @Date: 2021/8/18 23:02
 * @Package: com.weiwan.dsp.core.pub
 * @ClassName: JarScanner
 * @Description:
 **/
public class JarScanner implements AutoCloseable {

    private ClassLoader classLoader;
    private URL innerUrl;
    private FileInputStream fileInputStream;
    private JarInputStream jarInputStream;

    public JarScanner(ClassLoader classLoader, URL jarUrl) throws IOException {
        this.classLoader = classLoader;
        this.innerUrl = jarUrl;
        this.fileInputStream = new FileInputStream(innerUrl.getPath());
        this.jarInputStream = new JarInputStream(fileInputStream);
    }

    public List<URL> scanFiles(String prefix, String suffix) throws IOException {
        List<URL> res = new ArrayList<>();
        try {
            JarEntry nextJarEntry = jarInputStream.getNextJarEntry();
            while (null != nextJarEntry) {
                String filePath = nextJarEntry.getName();
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                if (fileName.startsWith(prefix) && fileName.endsWith(suffix)) {
                    URL resource = classLoader.getResource(filePath);
                    if (resource != null) {
                        res.add(resource);
                    }
                }
                nextJarEntry = jarInputStream.getNextJarEntry();
            }
            return res;
        } finally {
            fileInputStream.close();
            jarInputStream.close();
        }
    }

    public List<String> scanPackages(String packages) {
        return null;
    }

    @Override
    public void close() throws Exception {
        if(fileInputStream != null) {
            fileInputStream.close();
        }
        if(jarInputStream != null) {
            jarInputStream.close();
        }
    }
}
