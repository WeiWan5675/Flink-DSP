package com.weiwan.dsp.api.node;

import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.plugin.PluginContainer;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/9 18:31
 * @description: 读取数据节点
 */
public interface ReaderNode<T extends Serializable> extends Node {
    NodeType nodeType = NodeType.READER;

}
