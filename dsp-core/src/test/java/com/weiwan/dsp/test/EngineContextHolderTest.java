package com.weiwan.dsp.test;

import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.core.engine.EngineContextHolder;
import com.weiwan.dsp.core.engine.EngineRunContext;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 10:27
 * @description:
 */
public class EngineContextHolderTest {


    @Test
    public void testContextHolder(){
        try {
            //异常状态
            EngineContext context = EngineContextHolder.getContext();
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("异常信息: " + e.getMessage());
        }
        EngineRunContext dspRunContext = new EngineRunContext(null);
        dspRunContext.setJobName("测试");
        EngineContextHolder.init(dspRunContext);
        EngineContext context = EngineContextHolder.getContext();
        assertTrue(context != null);
        System.out.println(context.getJobName());


        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                try {
                    assertTrue(EngineContextHolder.getContext().getJobName() != null);
                    System.out.println("非当前线程获取到Context");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable2).start();
    }
}
