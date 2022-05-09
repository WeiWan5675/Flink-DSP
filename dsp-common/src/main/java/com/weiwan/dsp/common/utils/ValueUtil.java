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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ValueUtil {

    public static Integer getInt(Object obj) {
        if(obj == null) {
            return null;
        } else if(obj instanceof String) {
            return Integer.valueOf((String) obj);
        } else {
            try {
                Method method = obj.getClass().getMethod("intValue");
                return (int) method.invoke(obj);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Unable to convert " + obj + " into Interger",e);
            }
        }
    }

}
