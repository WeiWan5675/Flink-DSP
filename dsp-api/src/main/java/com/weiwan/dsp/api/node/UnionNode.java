package com.weiwan.dsp.api.node;

import com.weiwan.dsp.api.enums.NodeType;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 12:53
 * @description: 合并数据节点
 */
public interface UnionNode<T extends Serializable> extends Node {
    NodeType nodeType = NodeType.UNION;
}
