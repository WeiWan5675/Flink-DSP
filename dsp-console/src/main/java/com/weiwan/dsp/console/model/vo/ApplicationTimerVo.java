package com.weiwan.dsp.console.model.vo;

import lombok.Data;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/13 14:09
 * @Package: com.weiwan.dsp.console.model.vo
 * @ClassName: ApplicationTimerVo
 * @Description:
 **/
@Data
public class ApplicationTimerVo {
    private boolean enableStartTimer;
    private String startTimerCron;
    private boolean enableStopTimer;
    private String stopTimerCron;
    private boolean enableRestartTimer;
    private String restartTimerCron;
}
