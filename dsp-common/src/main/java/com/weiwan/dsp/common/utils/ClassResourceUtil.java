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


import java.io.File;
import java.net.URL;
import java.sql.DriverManager;


public class ClassResourceUtil {


    public final static String LOCK_STR = "class_lock_str";

    public static void forName(String clazz, ClassLoader classLoader) {
        synchronized (LOCK_STR) {
            try {
                Class.forName(clazz, true, classLoader);
                DriverManager.setLoginTimeout(10);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public synchronized static void forName(String clazz) {
        try {
            Class<?> driverClass = Class.forName(clazz);
            driverClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static URL findClassResourceByName(String fileName) {
        String _fileName = transformResourcePath(fileName);
        ClassLoader classLoader = ClassResourceUtil.class.getClassLoader();
        return classLoader.getResource(_fileName);
    }

    public static URL findClassResourceByName(String fileName, ClassLoader classLoader) {
        String _fileName = transformResourcePath(fileName);
        if(classLoader != null){
            return classLoader.getResource(_fileName);
        }else{
            ClassLoader thisClassLoader = ClassResourceUtil.class.getClassLoader();
            return thisClassLoader.getResource(_fileName);
        }
    }

    private static String transformResourcePath(String fileName) {
        String _fileName = fileName;
        if (fileName == null) {
            throw new RuntimeException("You need to specify a file name");
        }
        if (fileName.startsWith(File.separator)) {
            throw new RuntimeException("A relative path needs to be provided");
        }

        if (fileName.startsWith("classpath:")) {
            _fileName = fileName.replaceAll("classpath:", "");
        }
        if(fileName.indexOf("%5") != -1){
            _fileName = _fileName.replaceAll("%5", File.separator);
        }
        return _fileName;
    }
}
