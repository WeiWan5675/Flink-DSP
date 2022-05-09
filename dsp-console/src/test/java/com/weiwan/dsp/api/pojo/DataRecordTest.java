package com.weiwan.dsp.api.pojo;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.common.utils.FileUtil;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/10 18:33
 * @Package: com.weiwan.dsp.api.pojo
 * @ClassName: DataRecordTest
 * @Description:
 **/
public class DataRecordTest {


    @Test
    public void testDataRecord() throws IOException {
        String content = FileUtil.readFileContent("G:\\project\\Flink-DSP\\tmp\\jobs\\job_b82344d529d3a8b8d35b1f5b76576824.json");
        JSONObject jsonObject = JSONObject.parseObject(content);
        DataRecord dataRecord = new DataRecord(jsonObject);
        List field = dataRecord.getField("dsp.flow.nodes.fcf523c4-8cc4-41c1-8795-3ab8c2509853.nodeOutputDependents[1].[0].[0]", List.class);
        System.out.println(field);
    }


}