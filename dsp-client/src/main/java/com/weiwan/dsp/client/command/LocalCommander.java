package com.weiwan.dsp.client.command;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.enums.RunCmd;
import com.weiwan.dsp.core.shell.options.DspCliOptions;

public class LocalCommander extends ExecuteCommander{

    public LocalCommander(DspCliOptions option, DspConfig finalDspConfig) {
        super(option, finalDspConfig);
    }

    @Override
    public void initCMD(RunCmd runCmd) {

    }
}
