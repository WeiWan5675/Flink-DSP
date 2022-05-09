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

import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2020/8/12 17:46
 * @Package: com.weiwan.common.utils.FileUtil
 * @ClassName: FileUtil
 * @Description:
 **/
public class FileUtil {

    public static String readFileContent(String path) throws IOException {
        //读取配置文件  转化成json对象
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            throw new RuntimeException(String.format("The configuration file %s does not exist, please check the configuration file path!", file.getAbsolutePath()));
        }
        FileInputStream in = new FileInputStream(file);
        byte[] filecontent = new byte[(int) file.length()];
        in.read(filecontent);
        return new String(filecontent, "UTF-8");
    }

    public static String readFileContent(File file) throws IOException {
        //读取配置文件  转化成json对象
        if (!file.exists() || file.isDirectory()) {
            throw new RuntimeException(String.format("The configuration file %s does not exist, please check the configuration file path!", file.getAbsolutePath()));
        }
        FileInputStream in = new FileInputStream(file);
        byte[] filecontent = new byte[(int) file.length()];
        in.read(filecontent);
        return new String(filecontent, "UTF-8");
    }

    public static String readFileContent(URL url) throws IOException, URISyntaxException {
        File file = new File(url.toURI());
        return readFileContent(file);
    }

    public static BasicFileAttributes getFileAttributes(File file) throws IOException {
        Path path = file.toPath();
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        return attr;
    }

    public static String readFileContentFromJar(URL url) throws IOException {
        String content = null;
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            content = new String(buffer);
        } catch (Exception e) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PLUGIN_DEF_FILE_READ_IS_FAILED);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return content;
    }

    public static boolean isAbsolutePath(String path) {
        if (path != null && !path.equalsIgnoreCase("")) {
            if (path.startsWith("/")) {
                return true;
            }
            if (path.startsWith(":", 1)) {
                return true;
            }
        }
        return false;
    }


    public static boolean existsDir(String dirName) {
        if (dirName != null) {
            File file = new File(dirName);
            return file.exists() && file.isDirectory();
        }
        return false;
    }

    public static boolean existsFile(String fileName) {
        if (fileName != null) {
            return existsFile(new File(fileName));
        }
        return false;
    }

    public static boolean existsFile(File file) {
        return file.exists() && file.isFile();
    }

    public static boolean checkFileSuffix(String path, String... fileSuffix) {
        if (StringUtils.isNotEmpty(path)) {
            for (String suffix : fileSuffix) {
                if (path.endsWith(suffix)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void createDir(String fileDir) {
        if (fileDir != null) {
            File file = new File(fileDir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public static void delete(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public static void createDir(File file, boolean delete) {
        if (file != null) {
            if (file.exists() && file.isDirectory()) {
                //
                if (delete) {
                    file.delete();
                    file.mkdirs();
                }
            } else {
                file.mkdirs();
            }
        }
    }

    public static List<URL> toURLs(List<String> files) {
        List<URL> urls = new ArrayList<>();
        if (files != null) {
            for (String file : files) {
                File file1 = new File(file);
                if (file1.exists()) {
                    try {
                        urls.add(file1.toURI().toURL());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return urls;
    }

    public static void copy(File source, File dest, boolean overwrite) {
        if (overwrite) {
            if (dest.exists()) {
                dest.delete();
            }
        } else if (dest.exists()) {
            throw new IllegalStateException("target file already exists");
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];
            int len;

            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    in = null;
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    out = null;
                    e.printStackTrace();
                }
            }
        }
    }
}
