package com.weiwan.dsp.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.console.mapper.ApplicationDeployMapper;
import com.weiwan.dsp.console.mapper.ApplicationMapper;
import com.weiwan.dsp.console.model.entity.Application;
import com.weiwan.dsp.console.model.entity.ApplicationDeploy;
import com.weiwan.dsp.console.model.query.MetricQuery;
import com.weiwan.dsp.console.model.vo.ApplicationOverviewVo;
import com.weiwan.dsp.console.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/14 17:19
 * @ClassName: MetricsServiceImpl
 * @Description:
 **/
@Service
public class MetricsServiceImpl implements MetricsService {

    @Autowired
    private ApplicationMapper applicationMapper;


    @Autowired
    private ApplicationDeployMapper applicationDeployMapper;


    @Override
    public ApplicationOverviewVo getApplicationOverview(MetricQuery metricQuery) {
        ApplicationOverviewVo overviewVo = new ApplicationOverviewVo();
        List<Map<String, Object>> appStateMap = applicationMapper.selectMaps(groupByAppStateWrapper(metricQuery));
        for (Map<String, Object> map : appStateMap) {
            Integer appState = (Integer) map.get("app_state");
            Long count = (Long) map.get("count");
            if (appState != null && count != null) {
                ApplicationState applicationState = ApplicationState.getApplicationState(appState.intValue());
                calculateOverviewData(overviewVo, applicationState, count.intValue());
            }
        }
        Long needRestartCount = new LambdaQueryChainWrapper<>(applicationDeployMapper)
                .eq(ApplicationDeploy::getRestartMark, 1).count();
        if (needRestartCount != null) overviewVo.setNeedRestart(needRestartCount.intValue());
        Long deployedCount = new LambdaQueryChainWrapper<>(applicationDeployMapper)
                .isNotNull(ApplicationDeploy::getStartTime).count();
        if (deployedCount != null) overviewVo.setDeployed(deployedCount.intValue());
        return overviewVo;
    }

    private QueryWrapper<ApplicationDeploy> groupByRestartMarkWrapper(MetricQuery metricQuery, String field, String... fields) {
        QueryWrapper<ApplicationDeploy> qw = new QueryWrapper<>();
        qw.select(fields);
        setMetricQuery(metricQuery, qw);
        qw.eq("restart_mark", 1);
        qw.groupBy(field);
        return qw;
    }

    private void setMetricQuery(MetricQuery metricQuery, QueryWrapper<?> qw) {
        if (metricQuery.getAppId() != -1) {
            qw.eq("id", metricQuery.getAppId());
        }
        if (metricQuery.getStartTime() != null) {
            qw.ge("create_time", metricQuery.getStartTime());
        }
        if (metricQuery.getEndTime() != null) {
            qw.le("create_time", metricQuery.getEndTime());
        }
    }

    private QueryWrapper<Application> groupByAppStateWrapper(MetricQuery metricQuery) {
        QueryWrapper<Application> qw = new QueryWrapper<>();
        qw.select("app_state", "count(1) as count");//查询自定义列
        setMetricQuery(metricQuery, qw);
        qw.groupBy("app_state");
        return qw;
    }

    private void calculateOverviewData(ApplicationOverviewVo overviewVo, ApplicationState appState, Integer count) {
        overviewVo.setTotal(overviewVo.getTotal() + count);
        if (appState != null && count != null) {
            switch (appState) {
                case RUNNING:
                case STARTING:
                case RESTARTING:
                case CANCELING:
                case STOPPING:
                case WAITING:
                    overviewVo.setRunning(overviewVo.getRunning() + count);
                    break;
                case CANCELED:
                    overviewVo.setCanceled(overviewVo.getCanceled() + count);
                    break;
                case STOPPED:
                case FINISHED:
                    overviewVo.setFinished(overviewVo.getFinished() + count);
                    break;
                case FAILED:
                case ERROR:
                    overviewVo.setFailed(overviewVo.getFailed() + count);
                    break;
            }
        }
    }


}
