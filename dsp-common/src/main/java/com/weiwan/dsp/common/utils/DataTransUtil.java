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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DataTransUtil{

    /**
     * 交换map的key value
     * @param map
     * @return
     */
    public static Map<String, String> swapMapKeyValue(Map<String,String> map) {
        Map<String, String> hashMap=new HashMap<String, String>();
        Set<String> set = map.keySet();
        for (String s:set) {
            hashMap.put(map.get(s),s);
        }
        return hashMap;
    }

}
