package com.weiwan.dsp.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.DspContextConfig;
import com.weiwan.dsp.common.config.AbstractConfig;
import com.weiwan.dsp.common.constants.Constants;
import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.common.utils.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/14 11:09
 * @description:
 */
public class DspConfigFactory {

    public static DspConfig loadFromJson(String json) throws JsonProcessingException {
        //校验任务配置文件是否合法
        if (!JsonUtil.isJSONValid(json)) {
            throw new DspException("Illegal configuration file content, please check the configuration file format.");
        }
        return contentToDspConfig(json);
    }


    public static DspConfig load(File file) throws IOException {
        String configFileContent = readConfigFile(file);
        DspConfig dspConfig = loadFromJson(configFileContent);
        return dspConfig;
    }

    public static String readConfigFile(File file) throws IOException {
        String content = null;
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.endsWith(Constants.YML_FILE_SUFFIX) || absolutePath.endsWith(Constants.YAML_FILE_SUFFIX)) {
            String yamlContent = FileUtil.readFileContent(file);
            if (yamlContent != null) content = ConfConvertUtil.convertYaml2Json(yamlContent);
        } else if (absolutePath.endsWith(Constants.PROPERTIES_FILE_SUFFIX)) {
            String propContent = FileUtil.readFileContent(file);
            if (propContent != null) content = ConfConvertUtil.convertProp2Json(propContent);
        } else if (absolutePath.endsWith(Constants.JSON_FILE_SUFFIX)) {
            content = FileUtil.readFileContent(file);
        } else {
            //文件不可以是非法的文件类型
            throw new DspException(String.format("Unknown profile type. Only supported [%s | %s | %s]", Constants.JSON_FILE_SUFFIX, Constants.YAML_FILE_SUFFIX, Constants.PROPERTIES_FILE_SUFFIX));
        }
        if (content == null) {
            //文件内容不可以为空
            throw new DspException(String.format("The configuration file cannot be an empty file. Configuration file is %s", file.getName()));
        }
        return content;
    }


    public static DspConfig load(String filePath) throws IOException {
        if (StringUtils.isNotBlank(filePath)) {
            File file = new File(filePath);
            if (FileUtil.existsFile(file.getPath())) {
                return load(file);
            }else{
                throw DspException.generateIllegalStateException("The job file does not exist, the configuration object cannot be created");
            }
        }
        return new DspConfig();
    }

    public static DspConfig contentToDspConfig(String content) throws JsonProcessingException {
        DspContextConfig dspContextConfig = ObjectUtil.contentToObject(content, DspContextConfig.class);
        return dspContextConfig.getDsp();
    }


    public static String dspConfigToContent(DspConfig dspConfig) throws JsonProcessingException {
        DspContextConfig dspContextConfig = new DspContextConfig();
        dspContextConfig.setDsp(dspConfig);
        return ObjectUtil.objectToContent(dspContextConfig);
    }


    public static DspConfig createDefaultConfig(String dspConfDir) throws Exception {
        URL url = ClassResourceUtil.findClassResourceByName("conf" + "/" + DspConstants.DSP_CORE_CONF_FILE);
        String content = FileUtil.readFileContent(url);
        DspConfig classDspConfig = DspConfigFactory.loadFromJson(ConfConvertUtil.convertYaml2Json(content));
        String defaultConfigPath = dspConfDir + File.separator + DspConstants.DSP_CORE_CONF_FILE;
        String userDefaultConfig = FileUtil.readFileContent(new File(defaultConfigPath));
        DspConfig userDspConfig = DspConfigFactory.loadFromJson(ConfConvertUtil.convertYaml2Json(userDefaultConfig));
        return mergeJobConfig(classDspConfig, userDspConfig);
    }


    /**
     * 这里默认c2 覆盖 c1 存在优先级
     *
     * @param c1
     * @param c2
     * @return
     */
    public static DspConfig mergeJobConfig(DspConfig c1, DspConfig c2) {
        if (CheckTool.checkVersIsNull(c1, c2)) {
            CheckTool.throwNullPointException("JobConfig cannot be empty");
        }
        return mergeConfig(c1, c2);
    }

    public static DspConfig mergeConfig(DspConfig... configs) {
        CheckTool.checkIsNull(configs);
        if (configs.length < 2) {
            CheckTool.throwParameterErrorException("Need more than two Job Config");
        }

        JSONObject jsonObject = new JSONObject();
        for (DspConfig config : configs) {
            AbstractConfig.merge(jsonObject, ObjectUtil.beanToMap(config));
        }
        return ObjectUtil.mapToBean(jsonObject, DspConfig.class);
    }


    public static File writeJobFile(String dir, String fileName, String fileContent) throws IOException {
        File tmpDirFile = new File(dir);
        //如果文件目录存在就删除
        FileUtil.createDir(tmpDirFile, false);
        //拼接文件路径
        String filePath = dir + File.separator + fileName;
        File file = new File(filePath);
        //如果文件存在就删除
        if (FileUtil.existsFile(file)) {
            FileUtil.delete(file);
        }
        file.createNewFile();
        if (file.canWrite()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(fileContent);
            bw.close();
        }
        return file;
    }
}
