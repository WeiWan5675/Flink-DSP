package com.weiwan.dsp.console.service;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.UnresolvedHandlerType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.console.service.impl.TemplateServiceImpl;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Author: xiaozhennan
 * @Date: 2021/9/14 21:14
 * @Package: com.weiwan.dsp.console.service
 * @ClassName: TemplateServiceTest
 * @Description:
 **/
public class TemplateServiceTest {


    @Test
    public void testGetEngineConfigs(){
        TemplateServiceImpl templateService = new TemplateServiceImpl();
        JSONObject flinkEngineTemplate = templateService.getFlinkEngineTemplate(EngineMode.FLINK_ON_YARN_PER);
        System.out.println(flinkEngineTemplate);
    }



    @Test
    public void testGetUnresolvedHandlerConfigs(){
        TemplateServiceImpl templateService = new TemplateServiceImpl();
        JSONObject unresolvedCollectorTemplate = templateService.getUnresolvedCollectorTemplate(UnresolvedHandlerType.LOG_HANDLER);
        System.out.println(unresolvedCollectorTemplate.toJSONString());
    }

}