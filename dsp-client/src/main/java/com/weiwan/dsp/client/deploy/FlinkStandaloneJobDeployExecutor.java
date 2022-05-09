package com.weiwan.dsp.client.deploy;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.FlinkStandaloneConfigs;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.rest.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * @Author: xiaozhennan
 * @Date: 2021/10/19 14:57
 * @ClassName: FlinkStandaloneJobDeployExecutor
 * @Description:
 **/
public class FlinkStandaloneJobDeployExecutor extends FlinkJobDeployExecutor {
    private static final Logger logger = LoggerFactory.getLogger(FlinkStandaloneJobDeployExecutor.class);

    public FlinkStandaloneJobDeployExecutor(DspConfig dspConfig) {
        super(dspConfig);
    }


    private FlinkStandaloneConfigs flinkStandaloneConfigs;
    private URL runtimeJar;
    private List<URL> pluginJars;
    private URL jobDefFile;
    private ClusterClient clusterClient;
    private Configuration flinkConfiguration;

    @Override
    public synchronized void init() throws Exception {
        super.init();
        this.jobDefFile = getDspJob().getJobDefFile();
        this.pluginJars = getDspJob().getJobPluginJars();
        this.runtimeJar = getDspJob().getRuntimeJar();
        this.flinkStandaloneConfigs = new FlinkStandaloneConfigs(engineConfig.getEngineConfigs());
        logger.info("Load the Flink configuration file from the environment");
        this.flinkConfiguration = getFlinkConfiguration();
        logger.info("Set Flink's Checkpoint option according to configuration");
        super.checkpointSetting(flinkConfiguration, flinkStandaloneConfigs);
        logger.info("Set Flink's internal options according to the configuration");
        super.internalSetting(flinkConfiguration, flinkStandaloneConfigs);
        if (this.clusterClient == null) {
            synchronized (this) {
                StandaloneClusterDescriptor standaloneClusterDescriptor = new StandaloneClusterDescriptor(flinkConfiguration);
                this.clusterClient = standaloneClusterDescriptor
                        .retrieve(StandaloneClusterId.getInstance())
                        .getClusterClient();
                this.setJobWebUrl(clusterClient.getWebInterfaceURL());
            }
            logger.info("Flink Standalone Job Deploy Executor inited!");
        }

    }

    @Override
    public synchronized void close() throws Exception {
        super.close();
        clusterClient.close();
    }

    @Override
    public synchronized void start() throws Exception {
        JobStatus jobStatus = getJobStatus();
        if (jobStatus != null && !jobStatus.isGloballyTerminalState()) {
            TimeUnit.SECONDS.sleep(10);
            jobStatus = getJobStatus();
            if (jobStatus != null && !jobStatus.isGloballyTerminalState()) {
                logger.info("Failed to start the job, the job is in a non-final state on the cluster, please check the applications running on the cluster, jobID: {}", jobId);
                throw DspException.generateIllegalStateException("Failed to start the job, the job is in a non-final state on the cluster");
            }
        }

        logger.info("Obtain the dependent jars needed by Flink Job at runtime from [dsp.lib.dir]");
        SystemEnvManager instance = SystemEnvManager.getInstance();
        Collection<URL> urls = new LinkedHashSet<>();
        urls.addAll(instance.getDspRuntimeLibs());
        urls.addAll(this.pluginJars);
        List<URL> libs = new ArrayList<>(urls);
        logger.info("Obtain the Flink Standalone Mode Cluster Descriptor according to the configuration");
        logger.info("Build dsp streaming job packaged program");
        logger.info("Set Flink's Savepoint option according to configuration");
        SavepointRestoreSettings savepointRestoreSettings = savepointSetting(flinkConfiguration, flinkStandaloneConfigs);
        //覆盖配置
        PackagedProgram program =
                PackagedProgram.newBuilder()
                        .setUserClassPaths(libs)
                        .setJarFile(new File(runtimeJar.toURI()))
                        .setEntryPointClassName(JobDeployExecutor.DSP_RUNTIME_MAIN_CLASS)
                        .setConfiguration(flinkConfiguration)
                        .setArguments(jobDefFile.getPath())
                        .setSavepointRestoreSettings(savepointRestoreSettings)
                        .build();

        logger.info("Generate Flink Job Graph and wait for task submission");
        JobGraph jobGraph =
                PackagedProgramUtils.createJobGraph(
                        program,
                        flinkConfiguration,
                        flinkStandaloneConfigs.getJobParallelism(),
                        false);

        jobGraph.addJars(libs);
        jobGraph.setJobID(JobID.fromHexString(jobId));
        logger.info("Start to submit the job please wait");
        JobID jobID = (JobID) clusterClient.submitJob(jobGraph).get();
        logger.info("The job has been submitted job id: {}", jobID);
    }

    @Override
    public synchronized void stop() throws Exception {
        CompletableFuture<String> completableFuture = clusterClient.stopWithSavepoint(JobID.fromHexString(jobId), false, "hdfs://dev03/flink-savepoints");
        String savepointPath = completableFuture.get();
        if (StringUtils.isNotBlank(savepointPath)) {
            logger.info("job stops generating savepoint: {}", savepointPath);
        }
        logger.info("The job has been successfully stopped stopped, jobID: {}", jobId);
    }

    @Override
    public synchronized void cancel() throws Exception {
        CompletableFuture<Acknowledge> cancel = clusterClient.cancel(JobID.fromHexString(jobId));
        Acknowledge acknowledge = cancel.get();
        if (acknowledge != null) {
            logger.debug("The request to cancel the job has been received by the Flink cluster, JobId: {}", jobId);
        }
        logger.info("The job has been successfully canceled stopped, jobID: {}", jobId);
    }

    @Override
    public synchronized void refresh() throws Exception {
        logger.info("Request to refresh job status, jobID: {}", jobId);
        JobStatus jobStatus = getJobStatus();
        if (jobStatus == null) {
            return;
        }
        switch (jobStatus) {
            case FAILED:
                modifyApplicationState(ApplicationState.FAILED, true);
                logger.error("The application has stopped abnormally, please check the application status!");
                break;
            case RESTARTING:
            case CREATED:
            case INITIALIZING:
                modifyApplicationState(ApplicationState.STARTING, true);
                logger.info("The application has been created, please wait for the job to start");
                break;
            case CANCELED:
                modifyApplicationState(ApplicationState.CANCELED, true);
                logger.info("The application has finished canceling the job");
                break;
            case FINISHED:
                modifyApplicationState(ApplicationState.FINISHED, true);
                logger.info("The application has completed all tasks, and related tasks have been completed");
                break;
            case CANCELLING:
                modifyApplicationState(ApplicationState.CANCELING, true);
                logger.info("The application is canceling, please wait for the task to complete");
                break;
            case SUSPENDED:
            case FAILING:
            case RECONCILING:
                modifyApplicationState(ApplicationState.WAITING, true);
                logger.info("The current application is in a waiting state, please wait for a while for the job to complete");
                break;
            case RUNNING:
                if(getApplicationState() != ApplicationState.STOPPING && getApplicationState() != ApplicationState.CANCELING){
                    modifyApplicationState(ApplicationState.RUNNING, true);
                }
                logger.info("The current application is running normally");
                break;
            default:
                modifyApplicationState(ApplicationState.UNKNOWN, true);
                logger.warn("The current application is in an unknown state, please observe for a while.");
        }
    }

    private JobStatus getJobStatus() throws InterruptedException, ExecutionException {
        CompletableFuture<JobStatus> result = clusterClient.getJobStatus(JobID.fromHexString(jobId));
        JobStatus jobStatus = null;
        try {
            if (result != null) {
                JobStatus status = result.get();
                if (status != null) {
                    jobStatus = status;
                } else {
                    jobStatus = JobStatus.FINISHED;
                }
            }
        } catch (ExecutionException e) {
            if (e.getMessage().contains("NotFoundException:")) {
                logger.warn("the app could not be found on the cluster");
                jobStatus = JobStatus.FINISHED;
            } else {
                throw e;
            }
        } catch (Exception e) {
            logger.warn("The application status cannot be obtained temporarily, please check the cluster status", e);
        }
        return jobStatus;
    }
}
