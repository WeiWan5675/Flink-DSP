package com.weiwan.dsp.console.model.dto;

import com.weiwan.dsp.console.schedule.tasks.AppRestartTask;
import com.weiwan.dsp.console.schedule.tasks.AppStartTask;
import com.weiwan.dsp.console.schedule.tasks.AppStopTask;
import lombok.Data;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/4/14 16:36
 * @description
 */
@Data
public class ApplicationTimer {

    private Boolean enableTimer;
    private String timerCron;
    private ApplicationTimerType timerType;
    private Integer taskId;
    private String jobId;

    public enum ApplicationTimerType {
        START_TIMER(1, "startTimer", AppStartTask.class),
        STOP_TIMER(2, "stopTimer", AppStopTask.class),
        RESTART_TIMER(3, "restartTimer", AppRestartTask.class);

        private Integer code;
        private String type;
        private Class timerClass;

        ApplicationTimerType(Integer code, String type, Class timerClass) {
            this.code = code;
            this.type = type;
            this.timerClass = timerClass;
        }

        public Class getTimerClass() {
            return timerClass;
        }

        public Integer getCode() {
            return code;
        }

        public String getType() {
            return type;
        }
    }
}
