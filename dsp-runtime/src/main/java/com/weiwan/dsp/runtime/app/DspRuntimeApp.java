package com.weiwan.dsp.runtime.app;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.core.engine.DspJobFlowEngine;
import com.weiwan.dsp.core.engine.FlowEngineFactory;
import com.weiwan.dsp.core.flow.*;
import com.weiwan.dsp.core.engine.EngineContextHolder;
import com.weiwan.dsp.core.engine.EngineContextUtil;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/9 18:41
 * @description: run context app
 */
public class DspRuntimeApp {

    private static final Logger logger = LoggerFactory.getLogger(DspRuntimeApp.class);
    public static void main(String[] args) throws Exception {

        if (args == null || args.length < 1) {
            throw new RuntimeException("Start parameter error, start failed, please check");
        }
        String jobFilePath = args[0];
        if (StringUtils.isBlank(jobFilePath)) {
            throw new RuntimeException("Job file does not exist, please check");
        }
        if (!FileUtil.checkFileSuffix(jobFilePath, "json")) {
            throw new RuntimeException("Illegal job file. The job file needs to be a file ending in [tmp_xxx.json]");
        }
        File jobFile = new File(jobFilePath);
        if (!FileUtil.existsFile(jobFile)) {
            throw new RuntimeException("Unable to find job file from disk, please check");
        }

        logger.info("load job file: {}", jobFile);
        //读取配置文件
        DspConfig dspConfig = DspConfigFactory.load(jobFile);

        //组装DspContext
        EngineContext engineContext = EngineContextUtil.buildContext(dspConfig);
        //初始化context
        EngineContextHolder.init(engineContext);
        //创建DSP执行引擎
        DspJobFlowEngine engine = FlowEngineFactory.createEngine(engineContext);
        //初始化执行引擎
        engine.init();
        //创建作业图
        DspJobFlowGraph dspJobFlowGraph = DspJobFlowEngine.buildGraph(engineContext);
        if(dspJobFlowGraph != null){
            engine.execute(dspJobFlowGraph);
        }

        //关闭执行引擎,释放必要的资源
        engine.stop();
    }
}
