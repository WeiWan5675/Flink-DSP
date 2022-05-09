package com.weiwan.dsp.core.resolve.logging;

import com.weiwan.dsp.api.pojo.UnresolvedDataRecord;
import com.weiwan.dsp.core.resolve.DspUnresolvedDataCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 14:49
 * @description:
 */
public class LogUnresolvedDataCollector extends DspUnresolvedDataCollector<LogUnresolvedConfig> {


    public static final String WARN_LOG_TEMPLATE = "DSP-Unresolved-Data: {}";
    private Logger _LOGGER = null;
    private String logLevel;

    @Override
    public void init(LogUnresolvedConfig collectorConfig) {
        logLevel = collectorConfig.getLogLevel();
        _LOGGER = LoggerFactory.getLogger(LogUnresolvedDataCollector.class);
    }

    @Override
    public void stop() {
        //Nothing needs to be done
    }

    @Override
    public void handler(UnresolvedDataRecord record) {
        if (record == null) return;
        switch (logLevel.toLowerCase(Locale.ROOT)) {
            case "error":
                if (_LOGGER.isErrorEnabled()) _LOGGER.error(WARN_LOG_TEMPLATE, record);
                break;
            case "warn":
                if (_LOGGER.isWarnEnabled()) _LOGGER.warn(WARN_LOG_TEMPLATE, record);
                break;
            case "debug":
                if (_LOGGER.isDebugEnabled()) _LOGGER.debug(WARN_LOG_TEMPLATE, record);
                break;
            case "trace":
                if (_LOGGER.isTraceEnabled()) _LOGGER.trace(WARN_LOG_TEMPLATE, record);
                break;
            default:
                _LOGGER.info(WARN_LOG_TEMPLATE, record);
        }
    }
}
