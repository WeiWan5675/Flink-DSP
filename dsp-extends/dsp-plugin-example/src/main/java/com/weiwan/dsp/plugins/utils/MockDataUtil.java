package com.weiwan.dsp.plugins.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jiangbo
 * @date 2018/12/19
 */
public class MockDataUtil {

    private static AtomicLong id = new AtomicLong(0L);
    private static int minSize = 320;
    private static int maxSize = 320;
    private static MockConfig mockConfig = new MockConfig().subConfig(String.class).sizeRange(minSize, maxSize).globalConfig();

    public static Object mockData(String type) {
        Object mockData = null;
        try {
            switch (type.trim().toLowerCase()) {
                case "id":
                    mockData = id.incrementAndGet();
                    break;
                case "int":
                case "integer":
                    mockData = JMockData.mock(int.class);
                    break;
                case "byte":
                    mockData = JMockData.mock(byte.class);
                    break;
                case "boolean":
                    mockData = JMockData.mock(boolean.class);
                    break;
                case "char":
                case "character":
                    mockData = JMockData.mock(char.class);
                    break;
                case "short":
                    mockData = JMockData.mock(short.class);
                    break;
                case "long":
                    mockData = JMockData.mock(long.class);
                    break;
                case "float":
                    mockData = JMockData.mock(float.class);
                    break;
                case "double":
                    mockData = JMockData.mock(double.class);
                    break;
                case "date":
                    mockData = JMockData.mock(Date.class);
                    break;
                case "timestamp":
                    mockData = JMockData.mock(Timestamp.class);
                    break;
                case "bigdecimal":
                    mockData = JMockData.mock(BigDecimal.class);
                    break;
                case "biginteger":
                    mockData = JMockData.mock(BigInteger.class);
                    break;
                case "int[]":
                    mockData = JMockData.mock(int[].class);
                    break;
                case "byte[]":
                    mockData = JMockData.mock(byte[].class);
                    break;
                case "boolean[]":
                    mockData = JMockData.mock(boolean[].class);
                    break;
                case "char[]":
                case "character[]":
                    mockData = JMockData.mock(char[].class);
                    break;
                case "short[]":
                    mockData = JMockData.mock(short[].class);
                    break;
                case "long[]":
                    mockData = JMockData.mock(long[].class);
                    break;
                case "float[]":
                    mockData = JMockData.mock(float[].class);
                    break;
                case "double[]":
                    mockData = JMockData.mock(double[].class);
                    break;
                case "string[]":
                    mockData = JMockData.mock(String[].class);
                    break;
                case "binary":
                    String str = JMockData.mock(String.class, mockConfig);
                    mockData = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
                    break;
                case "jsonarray":
                    mockData = JMockData.mock(JSONArray.class);
                    break;
                case "jsonobject":
                    mockData = JMockData.mock(JSONObject.class);
                    break;
                default:
                    mockData = JMockData.mock(String.class);
                    break;
            }
        } catch (Exception e) {
        }
        return mockData;
    }


    private static Object getField(String value, String type) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }

        Object field;
        switch (type.toLowerCase()) {
            case "integer":
            case "smallint":
            case "tinyint":
            case "int":
                field = NumberUtils.toInt(value);
                break;
            case "bigint":
            case "long":
                field = NumberUtils.toLong(value);
                break;
            case "float":
                field = NumberUtils.toFloat(value);
                break;
            case "double":
                field = NumberUtils.toDouble(value);
                break;
            default:
                field = value;
        }

        return field;
    }
}