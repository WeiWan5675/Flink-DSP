package com.weiwan.dsp.console.schedule;

import com.weiwan.dsp.api.config.console.TaskConfigs;
import lombok.Data;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/5 22:40
 * @Package: com.weiwan.dsp.console.schedule
 * @ClassName: SystemScheduleTask
 * @Description: 内部任务
 **/
@Data
public class SystemScheduleTask {
    private String name;
    private String className;
    private String cronExpr;
    private TaskConfigs taskConfigs;
}
