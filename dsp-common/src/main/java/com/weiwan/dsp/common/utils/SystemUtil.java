/*
 *      Copyright [2020] [xiaozhennan1995@gmail.com]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *      http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weiwan.dsp.common.utils;

import com.weiwan.dsp.common.enums.EPlatform;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2020/7/16 15:43
 * @Package: com.weiwan.common.utils
 * @ClassName: SystemUtil
 * @Description:
 **/
public class SystemUtil {

    public static String getSystemVar(String name) {
        String property = System.getenv(name);
        if (StringUtils.isEmpty(property)) {
            property = System.getProperty(name);
        }

        return property;
    }


    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSystemUserName() {
        String sysUserName = "unknown";
        if (OSinfo.getOSname() == EPlatform.Windows) {
            sysUserName = System.getProperty("user.name");
        } else if (OSinfo.getOSname() == EPlatform.Linux) {
            sysUserName = System.getProperty("USER");
        }
        return sysUserName;
    }

    public static EPlatform getSystemOS() {
        if (OSinfo.getOSname() == EPlatform.Windows) {
            return EPlatform.Windows;
        } else if (OSinfo.getOSname() == EPlatform.Linux) {
            return EPlatform.Linux;
        }
        return EPlatform.Linux;
    }

    public static List<URL> findJarsInDir(File dir) throws MalformedURLException {
        List<URL> urlList = new ArrayList<>();

        if (dir.exists() && dir.isDirectory()) {
            File[] jarFiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jar");
                }
            });

            for (File jarFile : jarFiles) {
                urlList.add(jarFile.toURI().toURL());
            }

        }

        return urlList;
    }

    public static void setSystemVar(String key, String var) {
        System.setProperty(key, var);
    }
}
