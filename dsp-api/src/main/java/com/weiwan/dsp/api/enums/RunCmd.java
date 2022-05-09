package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: xiaozhennan
 * @Date: 2020/11/17 9:07
 * @Package: com.weiwan.support.launcher.enums.RunCmd
 * @ClassName: RunCmd
 * @Description:
 **/
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RunCmd {
    RUN,
    STOP,
    CANCEL,
    INFO,
    LIST;

    public static String print() {
        StringBuffer sb = new StringBuffer();
        RunCmd[] values = RunCmd.values();
        for (RunCmd value : values) {
            sb.append(value.name());
            sb.append("|");
        }
        String s = sb.toString();
        String substring = s.substring(0, s.lastIndexOf("|"));
        return substring;
    }

    @JsonCreator
    public static RunCmd from(String cmd) {
        if (StringUtils.isNotBlank(cmd)) {
            RunCmd[] values = values();
            for (RunCmd value : values) {
                if (value.name().equalsIgnoreCase(cmd)) {
                    return value;
                }
            }
        }
        throwUnknownCommandException(cmd);
        return null;
    }


    public static void throwUnknownCommandException(String cmd) {
        throw new RuntimeException("CMD: " + cmd + "is not supported");
    }
}