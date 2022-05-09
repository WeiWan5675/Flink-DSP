package com.weiwan.dsp.api.constants;

import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

/**
 * @author: xiaozhennan
 * @description:
 */
public class ClientConstants {
    public static final ConfigOption<String> JOB_ID = ConfigOptions.key("jobId").defaultValue("00000000000000000000000000000000").ok(String.class);
    public static final ConfigOption<String> JOB_NAME = ConfigOptions.key("jobName").defaultValue("DspStreamJob").ok(String.class);
    public static final ConfigOption<String> JOB_FILE = ConfigOptions.key("jobFile").ok(String.class);
    public static final ConfigOption<String> JOB_TMP_DIR = ConfigOptions.key("jobTmpDir").ok(String.class);
}
