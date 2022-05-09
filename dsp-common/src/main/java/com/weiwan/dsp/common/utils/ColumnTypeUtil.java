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

import java.util.Objects;


public class ColumnTypeUtil {

    private static final String TYPE_NAME = "decimal";
    private static final String LEFT_BRACKETS = "(";
    private static final String RIGHT_BRACKETS = ")";
    private static final String DELIM = ",";

    public static boolean isDecimalType(String typeName){
        return typeName.toLowerCase().startsWith(TYPE_NAME);
    }

    public static DecimalInfo getDecimalInfo(String typeName, DecimalInfo defaultInfo){
        if(!isDecimalType(typeName)){
            throw new IllegalArgumentException("Unsupported column type:" + typeName);
        }

        if (typeName.contains(LEFT_BRACKETS) && typeName.contains(RIGHT_BRACKETS)){
            int precision = Integer.parseInt(typeName.substring(typeName.indexOf(LEFT_BRACKETS) + 1,typeName.indexOf(DELIM)).trim());
            int scale = Integer.parseInt(typeName.substring(typeName.indexOf(DELIM) + 1,typeName.indexOf(RIGHT_BRACKETS)).trim());
            return new DecimalInfo(precision, scale);
        } else {
            return defaultInfo;
        }
    }

    public static class DecimalInfo {
        private int precision;
        private int scale;

        public DecimalInfo(int precision, int scale) {
            this.precision = precision;
            this.scale = scale;
        }

        public int getPrecision() {
            return precision;
        }

        public int getScale() {
            return scale;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()){
                return false;

            }
            DecimalInfo that = (DecimalInfo) o;
            return precision == that.precision && scale == that.scale;
        }

        @Override
        public int hashCode() {
            return Objects.hash(precision, scale);
        }
    }
}
