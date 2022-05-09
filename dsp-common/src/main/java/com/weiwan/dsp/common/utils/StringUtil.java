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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2020/2/28 17:54
 * @Package: com.hopson.dc.common.utils
 * @ClassName: StringUtils
 * @Description:
 **/
public class StringUtil {

    /**
     * 判断多个字符串是否相等，如果其中有一个为空字符串或者null，则返回false，只有全相等才返回true
     */
    public static boolean isEquals(String... agrs) {
        String last = null;
        for (int i = 0; i < agrs.length; i++) {
            String str = agrs[i];
            if (isEmpty(str)) {
                return false;
            }
            if (last != null && !str.equalsIgnoreCase(last)) {
                return false;
            }
            last = str;
        }
        return true;
    }


    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isNotEmpty(String... strs) {
        for (String str : strs) {
            if (isEmpty(str)) {
                return false;
            }
        }
        return true;
    }


    public static String sortStrByDict(String str) {
        if (str != null && str.length() > 0) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            return String.valueOf(chars);
        }
        return null;
    }


    /**
     * 给字符串进行字典序排序
     *
     * @param str
     * @return
     */
    public static String dictSort(String str) {
        char[] chars = str.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    /**
     * 给字符串数组进行字典序排序
     *
     * @param strArr
     * @return
     */
    public static List<String> dictSort(String[] strArr) {
        List<String> list = Arrays.asList(strArr);
        Collections.sort(list);
        return list;
    }
}
