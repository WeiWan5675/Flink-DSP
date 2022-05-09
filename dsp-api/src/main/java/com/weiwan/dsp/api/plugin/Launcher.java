package com.weiwan.dsp.api.plugin;

import com.weiwan.dsp.api.context.DspSupport;

import java.io.Closeable;
import java.util.List;


/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/9 14:22
 * @description:
 */
public interface Launcher<T> extends DspSupport , Closeable {

    void launch(T row) throws Exception;

    void launch(List<T> rows) throws Exception;

    boolean isOpen();
}
