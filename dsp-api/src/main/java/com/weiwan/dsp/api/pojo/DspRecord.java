package com.weiwan.dsp.api.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/26 22:52
 * @ClassName: DspRecord
 * @Description: 数据接口
 **/
public interface DspRecord<T extends Serializable> extends Serializable {

    List<T> getFields();

    String serialize();

    T deserialize(String content);

    byte[] serializeBinary();

    T deserializeBinary(byte[] content);
}
