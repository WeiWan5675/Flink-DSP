package com.weiwan.dsp.plugins.split;

import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.core.plugin.RichSplitPlugin;
import com.weiwan.dsp.plugins.config.JavaScriptSplitPluginConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;

/**
 * @author: xiaozhennan
 * @date: 2021/6/22 9:36
 * @description:
 */
public class JavaScriptSplitPlugin extends RichSplitPlugin<JavaScriptSplitPluginConfig> {

    private static final Logger logger = LoggerFactory.getLogger(JavaScriptSplitPlugin.class);

    private static final String SCRIPT_ENGINE_NAME = "nashorn";
    private static final String METHOD_NAME = "match";
    private static final String MATCH_SCRIPT_FORMAT = "function match (record) { %s };";
    private ScriptEngine scriptEngine;
    private Invocable invocable;
    private String javaScriptContent;

    @Override
    public void init(JavaScriptSplitPluginConfig config) {
        logger.debug("JavaScript split plugin init function running.");
        //获取脚本引擎
        this.scriptEngine = new ScriptEngineManager().getEngineByName(SCRIPT_ENGINE_NAME);
        //初始化脚本内容
        String content = config.getJavaScriptContent();
        //校验脚本内容, 如果为空则给默认脚本, 从而保证脚本一定可用
        if (StringUtils.isNotBlank(content)) {
            this.javaScriptContent = String.format(MATCH_SCRIPT_FORMAT, content);
        } else {
            logger.warn("JavaScript content is blank, plugin class: {}", config.getPluginClass());
            this.javaScriptContent = String.format(MATCH_SCRIPT_FORMAT, "return false;");
        }
        try {
            //向引擎中注入脚本
            this.scriptEngine.eval(this.javaScriptContent);
            //将引擎向上转换成可调用接口
            this.invocable = (Invocable) this.scriptEngine;
        } catch (ScriptException e) {
            logger.error("Failed to init JavaScript engine, plugin class: {}", config.getPluginClass(), e);
        }
    }

    @Override
    public boolean match(DataRecord record) {
        try {
            //调用match方法处理record
            Object result = this.invocable.invokeFunction(METHOD_NAME, record);
            if (result != null && result instanceof Boolean) {
                return (boolean) result;
            }
            logger.debug("The JavaScript does not return a boolean value, JavaScript content: {}", javaScriptContent);
        } catch (Exception e) {
            logger.error("Failed to match JavaScript engine, record info: {}", record, e);
        }
        return false;
    }

    @Override
    public void stop() {
        logger.debug("JavaScript split plugin stop function running.");
    }

}
