package com.weiwan.dsp.test;

import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.ConfConvertUtil;
import org.junit.Test;

import java.io.IOException;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/14 14:32
 * @description:
 */
public class ConfConvertTest {


    @Test
    public void testJson2Yaml() throws IOException {
        String content = FileUtil.readFileContent("D:\\develop\\github\\Flink-DSP\\dsp-core\\src\\main\\resources\\job-flow.json");
        System.out.println(content);
        System.out.println("========================================");
        String s = ConfConvertUtil.convertJson2Yaml(content);
        System.out.println(s);
    }


    @Test
    public void testJson2Prop() throws IOException {
        String content = FileUtil.readFileContent("D:\\develop\\github\\Flink-DSP\\dsp-core\\src\\main\\resources\\job-flow.json");
        System.out.println(content);
        System.out.println("========================================");
        String s = ConfConvertUtil.convertJson2Prop(content);
        System.out.println(s);
    }

    @Test
    public void testProp2Json() throws IOException {
        String content = FileUtil.readFileContent("D:\\develop\\github\\Flink-DSP\\dsp-core\\src\\main\\resources\\job-flow.json");
        System.out.println(content);
        System.out.println("========================================");
        String s = ConfConvertUtil.convertJson2Prop(content);
        System.out.println(s);
        System.out.println("=========================================");
        String s1 = ConfConvertUtil.convertProp2Json(s);
        System.out.println(s1);

    }

}
