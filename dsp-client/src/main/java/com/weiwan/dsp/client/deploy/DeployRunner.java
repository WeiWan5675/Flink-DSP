package com.weiwan.dsp.client.deploy;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.CoreConfig;
import com.weiwan.dsp.api.config.core.JobConfig;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.core.deploy.DeployExecutor;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/30 14:51
 * @description: 提交DeployExecutor的类
 */
public class DeployRunner implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(DeployRunner.class);
    private final ExecutorService executorService;
    private final DeployExecutor deployExecutor;
    private final DspConfig dspConfig;
    private final JobConfig jobConfig;
    private final CoreConfig coreConfig;


    private Future<CommandResult> resultFuture;
    private long executionTimeout;
    public DeployRunner(DeployExecutor deployExecutor, DspConfig dspConfig) {
        this.dspConfig = dspConfig;
        this.coreConfig = dspConfig.getCore();
        this.jobConfig = dspConfig.getJob();
        this.deployExecutor = deployExecutor;
        this.executorService = Executors.newFixedThreadPool(1);
        initializeRunner();

    }

    private void initializeRunner() {
        this.executionTimeout = jobConfig.getJobDeployTimeout();
    }

    public void start() throws DspException {
        if (deployExecutor == null){
            throw new DspException("At least one deploy executor needs to be provided");
        }

        try {
            if(!checkDeployReady(deployExecutor)){
                logger.error("Unable to complete the initialization work before job submission");
                throw new DspException("DeployExecutor cannot complete initialization");
            }
        } catch (Exception e) {
            throw new DspException("DeployExecutor cannot complete initialization", e);
        }

        logger.info("Start to execute the Job submitted by the client");
//        this.resultFuture = executorService.submit(deployExecutor);
        waitForJobResults();

    }

    private void waitForJobResults() {
        while (true){
            try {
                CommandResult commandResult = resultFuture.get(executionTimeout, TimeUnit.SECONDS);
                if(commandResult.isSuccess()){
                    //部署成功
                    logger.info("The job submitted by the client has been completed");
                    break;
                }else{
                    //部署失败
                    logger.error("The job submitted by the client failed to execute , please check the specific log!", commandResult.getThrowable());
                    throw new DspException("job execute failed");
                }
            } catch (InterruptedException e) {
                logger.error("The job process was interrupted abnormally", e);
                throw new DspException("Job process was interrupted", e);
            } catch (ExecutionException e) {
                logger.error("The Job is abnormal, please check the detailed log", e);
                throw new DspException("Job execution failure!", e);
            } catch (TimeoutException e) {
                logger.error("Job Timed out,the default timeout is 90s please check the log", e);
                throw new DspException("Job timed out!", e);
            }
        }
    }


    private boolean checkDeployReady(DeployExecutor deployExecutor) throws Exception {
        JobDeployExecutor executor = (JobDeployExecutor) deployExecutor;
        return true;
    }


    @Override
    public void close() {
        if(deployExecutor != null){
            logger.info("Release related resources for Job submission.");
            JobDeployExecutor jobDeployExecutor = (JobDeployExecutor) deployExecutor;
        }
        if (executorService != null) executorService.shutdown();
        logger.info("The resource occupied by the Job submission has been released and completed.");
    }
}
