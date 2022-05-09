package com.weiwan.dsp.console.model.vo;

import lombok.Data;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/14 17:16
 * @ClassName: ApplicationOverviewVo
 * @Description:
 **/
@Data
public class ApplicationOverviewVo {
    //运行中
    private Integer running = 0;
    //已部署
    private Integer deployed = 0;
    //需要重启
    private Integer needRestart = 0;
    //总应用
    private Integer total = 0;
    //取消
    private Integer canceled = 0;
    //完成
    private Integer finished = 0;
    //失败
    private Integer failed = 0;
}
