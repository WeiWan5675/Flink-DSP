package com.weiwan.dsp.plugins.commons;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.core.plugin.RichProcessPlugin;
import com.weiwan.dsp.plugins.config.FieldRenamePluginConfig;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/4/11 15:48
 * @description
 */
public class FieldRenamePlugin extends RichProcessPlugin<FieldRenamePluginConfig> {

    private static final Logger logger = LoggerFactory.getLogger(FieldRenamePlugin.class);

    private Map<String, String> renameMap = new HashMap<>();

    @Override
    public void init(FieldRenamePluginConfig config) {
        logger.debug("Field remove plugin init function is running");
        List renameFields = config.getRenameFields();
        for (Object renameField : renameFields) {
            JSONObject object = new JSONObject((Map) renameField);
            String oldName = object.getString("oldName");
            String newName = object.getString("newName");
            renameMap.put(oldName, newName);
        }
    }

    @Override
    public DataRecord handle(DataRecord record) {
        //从record中查找字段, 如果有则替换, 如果无则返回原数据
        for (String key : renameMap.keySet()) {
            //查找字段是否存在,
            Object field = record.getField(key, Object.class);
            if (field != null) {
                record.addField(renameMap.get(key), field);
                record.removeField(key);
            }
        }
        return record;
    }

    public static void main(String[] args) throws IOException {
        String content = FileUtil.readFileContent("F:\\project\\Flink-DSP\\dsp-extends\\dsp-plugin-commons\\src\\main\\resources\\Dsp-Plugin-Rename.json");
        FieldRenamePluginConfig config = ObjectUtil.deSerialize(content, FieldRenamePluginConfig.class);
        FieldRenamePlugin plugin = new FieldRenamePlugin();
        plugin.init(config);

        String jobContent = FileUtil.readFileContent("F:\\project\\Flink-DSP\\tmp\\jobs\\job_b82344d529d3a8b8d35b1f5b76576824.json");
        JSONObject jsonObject = JSONObject.parseObject(jobContent);
        DataRecord dataRecord = new DataRecord(jsonObject);
        DataRecord record = plugin.handle(dataRecord);
        System.out.println(record);
    }

    @Override
    public void stop() {

    }
}
