package com.weiwan.dsp.core.utils;

import com.weiwan.dsp.api.constants.CoreConstants;
import com.weiwan.dsp.common.constants.Constants;
import com.weiwan.dsp.common.utils.MD5Utils;
import com.weiwan.dsp.common.utils.StringUtil;

/**
 * @author: xiaozhennan
 * @description:
 */
public class DspJobUtil {

    public static String getJobID(String jobName) {
        String str = String.format(CoreConstants.JOB_ID_FORMAR, jobName);
        String s = StringUtil.dictSort(str);
        String jobId = MD5Utils.md5(s);
        return jobId;
    }

}
