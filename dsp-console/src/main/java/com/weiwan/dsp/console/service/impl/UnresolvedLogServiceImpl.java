package com.weiwan.dsp.console.service.impl;

import com.weiwan.dsp.api.pojo.UnresolvedDataRecord;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.console.mapper.UnresolvedLogMapper;
import com.weiwan.dsp.console.model.entity.UnresolvedLog;
import com.weiwan.dsp.console.service.UnresolvedLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/9 18:43
 * @description
 */
@Service
public class UnresolvedLogServiceImpl implements UnresolvedLogService {

    @Autowired
    private UnresolvedLogMapper unresolvedLogMapper;

    @Override
    public void insertLog(UnresolvedDataRecord record) {
        //校验record是否为空
        CheckTool.checkIsNull(record, DspResultStatus.PARAM_IS_NULL);
        //不为空则把record转换为entity
        UnresolvedLog log = new UnresolvedLog();
        log.setJobId(record.getJobId());
        log.setJobName(record.getJobName());
        log.setNodeId(record.getNodeId());
        log.setNodeName(record.getNodeName());
        log.setNodeType(record.getNodeType().getCode());
        String unresolvedType = record.getUnresolvedType();
        String[] sourceAndType = unresolvedType.split("[.]");
        log.setUnresolvedSource(sourceAndType[0]);
        log.setUnresolvedType(sourceAndType[1]);
        log.setUnresolvedMsg(record.getMsg());
        long timestamp = record.getTimestamp();
        try {
            String date = new SimpleDateFormat(("yyyy-MM-dd")).format(timestamp);
            log.setUnresolvedDate(date);
            log.setUnresolvedTime(new Date(timestamp));
            log.setCreateTime(new Date());
            //保存到数据库
            unresolvedLogMapper.insert(log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
