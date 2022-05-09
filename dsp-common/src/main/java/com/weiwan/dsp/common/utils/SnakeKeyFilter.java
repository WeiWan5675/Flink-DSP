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

import com.alibaba.fastjson.serializer.NameFilter;

/**
 * @author: xiaozhennan
 * @date: 2019-01-05 18:31
 * @description:
 **/
public class SnakeKeyFilter implements NameFilter {

    public String process(Object o, String name, Object value) {
        return Underline2CamelUtil.camel2Underline(name);
    }
}
