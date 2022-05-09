package com.weiwan.dsp.console.model.vo;

import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/31 16:23
 * @ClassName: ApplicationDeployVo
 * @Description:
 **/
@Data
public class ApplicationDeployVo implements Serializable {


    private EngineType engineType;

    private EngineMode engineMode;

    private Integer id;

    private String jobId;

    private String jobName;

    private Integer appId;

    private String webUrl;

    private ApplicationState appState;

    private Long startTime;

    private String jobFile;

    private Long endTime;

    private Integer restartMark;

    private Long restartTime;

    private Integer userId;

    public String getJobFile() {
        return jobFile;
    }

    public void setJobFile(String jobFile) {
        this.jobFile = jobFile;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    public EngineMode getEngineMode() {
        return engineMode;
    }

    public void setEngineMode(EngineMode engineMode) {
        this.engineMode = engineMode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public ApplicationState getAppState() {
        return appState;
    }

    public void setAppState(ApplicationState appState) {
        this.appState = appState;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getRestartMark() {
        return restartMark;
    }

    public void setRestartMark(Integer restartMark) {
        this.restartMark = restartMark;
    }

    public Long getRestartTime() {
        return restartTime;
    }

    public void setRestartTime(Long restartTime) {
        this.restartTime = restartTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
