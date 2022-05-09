package com.weiwan.dsp.core.engine.ext;

import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface EngineExtInfo extends Serializable {

    public EngineType getEngineType();

    public EngineMode getEngineMode();
}
