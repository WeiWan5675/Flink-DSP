package com.weiwan.dsp.console.service.impl.deploy;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.JobConfig;
import com.weiwan.dsp.core.deploy.DeployExecutor;
import com.weiwan.dsp.client.deploy.DeployerFactory;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.common.utils.ThreadUtil;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @Author: xiaozhennan
 * @Date: 2021/10/19 23:02
 * @ClassName: StartStandaloneApplicationEventTest
 * @Description:
 **/
public class StartStandaloneApplicationEventTest extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(StartStandaloneApplicationEventTest.class);

    private final ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            200,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            ThreadUtil.threadFactory("dsp-deploy-executor", false, ThreadUtil.getLogUncaughtExceptionHandler("deploy")),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Test
    public void testJobDeployStandaloneStart() throws IOException, InterruptedException {

        DspConfig dspConfig = DspConfigFactory.load("G:\\project\\Flink-DSP\\dsp-console\\src\\test\\resources\\job_standalone.json");
        HashMap<String, String> tmp = new HashMap<>();
        tmp.put("dsp.lib.dir", "G:\\project\\Flink-DSP\\target\\Flink-DSP-1.0.0\\lib");
        tmp.putAll(System.getenv());
        SystemEnvManager.getInstance(tmp);
        JobConfig job = dspConfig.getJob();
        job.setJobPluginJars(Arrays.asList("G:\\project\\Flink-DSP\\target\\Flink-DSP-1.0.0\\plugin\\dsp-plugin-example-1.0.0.jar"));
        DeployExecutor deployer = DeployerFactory.createDeployer(dspConfig);


        StartApplicationEvent startApplicationEvent = new StartApplicationEvent((JobDeployExecutor) deployer, null);
        executorService.submit(startApplicationEvent);
        logger.info("当前作业状态: {}", deployer.getApplicationState());
        logger.info("当前作业WebUrl: {}", ((JobDeployExecutor) deployer).getJobWebUrl());
        TimeUnit.SECONDS.sleep(60);

        RefreshApplicationEvent refreshApplicationEvent = new RefreshApplicationEvent((JobDeployExecutor) deployer, null);
        executorService.submit(refreshApplicationEvent);
        logger.info("当前作业状态: {}", deployer.getApplicationState());


        TimeUnit.HOURS.sleep(1);

        System.out.println("exit");
//
//        StopApplicationEvent stopApplicationEvent = new StopApplicationEvent((JobDeployExecutor) deployer, null);
//        executorService.submit(stopApplicationEvent);
//        TimeUnit.SECONDS.sleep(30);
//        logger.info("当前作业状态: {}", deployer.getApplicationState());
//
//        RefreshApplicationEvent refreshApplicationEvent2 = new RefreshApplicationEvent((JobDeployExecutor) deployer, null);
//        executorService.submit(refreshApplicationEvent2);
//        logger.info("当前作业状态: {}", deployer.getApplicationState());
//
//
//        StartApplicationEvent startApplicationEvent2 = new StartApplicationEvent((JobDeployExecutor) deployer, null);
//        executorService.submit(startApplicationEvent2);
//        TimeUnit.SECONDS.sleep(60);
//        logger.info("当前作业状态: {}", deployer.getApplicationState());
//
//        RefreshApplicationEvent refreshApplicationEvent3 = new RefreshApplicationEvent((JobDeployExecutor) deployer, null);
//        executorService.submit(refreshApplicationEvent3);
//        logger.info("当前作业状态: {}", deployer.getApplicationState());
//
//
//        CancelApplicationEvent cancelApplicationEvent = new CancelApplicationEvent((JobDeployExecutor) deployer, null);
//        executorService.submit(cancelApplicationEvent);
//        TimeUnit.SECONDS.sleep(10);
//        logger.info("当前作业状态: {}", deployer.getApplicationState());
//
//        RefreshApplicationEvent refreshApplicationEvent4 = new RefreshApplicationEvent((JobDeployExecutor) deployer, null);
//        executorService.submit(refreshApplicationEvent4);
//        TimeUnit.SECONDS.sleep(10);
//        logger.info("当前作业状态: {}", deployer.getApplicationState());
//

        executorService.shutdown();

    }

}