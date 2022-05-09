package com.weiwan.dsp.api.node;

import com.weiwan.dsp.api.enums.NodeType;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/9 18:32
 * @description: 写出数据节点
 */
public interface WriterNode<T extends Serializable> extends Node{
    NodeType nodeType = NodeType.WRITER;
}
