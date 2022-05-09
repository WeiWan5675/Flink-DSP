package com.weiwan.dsp.console.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @Author: xiaozhennan
 * @Date: 2022/2/22 12:23
 * @Package: com.weiwan.dsp.console.util
 * @ClassName: MyBatisPlusGenerator
 * @Description: mybatis代码生成
 **/
public class MyBatisPlusGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://rm-2ze8v9g2o013w44f8wo.mysql.rds.aliyuncs.com:3306/flink_dsp?characterEncoding=utf-8&useSSL=false", "weiwan", "weiwan=123")
                .globalConfig(builder -> {
                    builder.author("xiaozhennan") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("./"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.weiwan.dsp.console.mapper") // 设置父包名
                            .moduleName("dsp-console") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "./")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("sys_schedule_job"); // 设置需要生成的表名
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
