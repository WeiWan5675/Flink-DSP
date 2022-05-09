package com.weiwan.dsp.console.init;

import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;
import com.weiwan.dsp.console.service.ApplicationDeployService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/9 18:29
 * @Package: com.weiwan.dsp.console.init
 * @ClassName: ApplicationStateRetrieveRunner
 * @Description: 恢复应用状态
 **/
@Component
public class AppStateRetrieveRunner implements CommandLineRunner {

    @Autowired
    private ApplicationDeployService deployService;
    private static final Logger logger = LoggerFactory.getLogger(AppStateRetrieveRunner.class);
    @Override
    public void run(String... args) throws Exception {
        logger.info("Start job status retrieval for jobs in running state");
        List<ApplicationDeployDTO> applicationDeploys = deployService.searchUnsettledDeploy();
        for (ApplicationDeployDTO deployDTO : applicationDeploys) {
            deployService.refreshDeploy(deployDTO.getId());
        }
    }
}
