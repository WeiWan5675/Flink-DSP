package com.weiwan.dsp.console.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.dsp.api.config.core.PluginConfigs;
import com.weiwan.dsp.api.enums.PluginJarOrigin;
import com.weiwan.dsp.api.enums.PluginType;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.enums.EPlatform;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.common.utils.*;
import com.weiwan.dsp.console.mapper.FlowMapper;
import com.weiwan.dsp.console.mapper.PluginJarMapper;
import com.weiwan.dsp.console.mapper.PluginMapper;
import com.weiwan.dsp.console.mapper.FlowPluginRefMapper;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.dto.PluginDTO;
import com.weiwan.dsp.console.model.entity.*;
import com.weiwan.dsp.console.model.query.PageQuery;
import com.weiwan.dsp.console.model.query.PluginQuery;
import com.weiwan.dsp.console.model.vo.PluginJarFileVo;
import com.weiwan.dsp.console.model.vo.PluginJarVo;
import com.weiwan.dsp.console.model.vo.PluginListVo;
import com.weiwan.dsp.console.model.vo.PluginVo;
import com.weiwan.dsp.console.service.PluginService;
import com.weiwan.dsp.console.service.UserService;
import com.weiwan.dsp.core.plugin.DspPluginManager;
import com.weiwan.dsp.core.plugin.container.PluginClassLoader;
import com.weiwan.dsp.core.pub.JarScanner;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: xiaozhennan
 * @Date: 2021/9/10 23:25
 * @Package: com.weiwan.dsp.console.service.impl
 * @ClassName: PluginServiceImpl
 * @Description:
 **/
@Service
public class PluginServiceImpl extends ServiceImpl<PluginJarMapper, PluginJar>
        implements PluginService {

    private static final Logger logger = LoggerFactory.getLogger(PluginServiceImpl.class);

    private final DspPluginManager pluginManager = DspPluginManager.getInstance();

    @Autowired
    private PluginJarMapper pluginJarMapper;

    @Autowired
    private PluginMapper pluginMapper;

    @Autowired
    private FlowPluginRefMapper flowPluginRefMapper;

    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private UserService userService;

    private LambdaQueryChainWrapper<Plugin> getPluginQueryWrapper(PluginQuery query) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPlugin(PluginJarVo pluginJarVo) {
        //检查pluginJarId不为空
        String pluginJarId = pluginJarVo.getPluginJarId();
        if (StringUtils.isBlank(pluginJarId)) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PARAM_IS_NULL);
        }
        //检查url不为空
        String pluginJarUrl = pluginJarVo.getPluginJarUrl();
        if (StringUtils.isBlank(pluginJarUrl)) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PARAM_IS_NULL);
        }
        //检查url指向的file存在
        File pluginJarFile = null;
        try {
            pluginJarFile = new File(new URI(pluginJarUrl));
            if (!pluginJarFile.exists()) {
                throw DspConsoleException.generateIllegalStateException(DspResultStatus.PLUGIN_JAR_FILE_NOT_EXISTS);
            }
            //检查md5存在
            String pluginJarMd5 = pluginJarVo.getPluginJarMd5();
            if (StringUtils.isBlank(pluginJarMd5)) {
                throw DspConsoleException.generateIllegalStateException(DspResultStatus.PARAM_IS_NULL);
            }
            //检查md5在表中不存在
            PluginJar md5Check = new LambdaQueryChainWrapper<PluginJar>(pluginJarMapper)
                    .eq(PluginJar::getPluginJarMd5, pluginJarVo.getPluginJarMd5())
                    .one();
            CheckTool.checkIsNotNull(md5Check, DspResultStatus.PLUGIN_JAR_EXISTS);

            List<PluginVo> pluginVos = pluginJarVo.getPlugins();
            List<Plugin> plugins = new ArrayList<>();
            for (PluginVo pluginVo : pluginVos) {
                Plugin plugin = new Plugin();
                String pluginName = pluginVo.getPluginName();
                String pluginClass = pluginVo.getPluginClass();
                String pluginId = MD5Utils.md5(String.format(ConsoleConstants.PLUGIN_ID_MD5_FORMAT, pluginName, pluginClass));
                plugin.setPluginJarId(pluginJarId);
                plugin.setPluginName(pluginName);
                plugin.setPluginClass(pluginClass);
                plugin.setPluginId(pluginId);
                plugin.setPluginType(pluginVo.getPluginType().getCode());
                plugin.setPluginAlias(pluginVo.getPluginAlias());
                plugin.setPluginDescription(pluginVo.getPluginDescription());
                plugin.setPluginConfigs(ObjectUtil.objectToContent(pluginVo.getPluginConfigs()));
                plugin.setPluginInfos(ObjectUtil.objectToContent(pluginVo.getPluginInfos()));
                plugin.setCreateTime(pluginJarVo.getCreateTime() == null ? new Date() : pluginJarVo.getCreateTime());
                plugin.setUpdateTime(pluginJarVo.getUpdateTime() == null ? new Date() : pluginJarVo.getUpdateTime());
                plugins.add(plugin);
            }
            //修改临时url为最终目录的url
            String dspPluginDir = SystemEnvManager.getInstance().getDspPluginDir();
            String finalPluginJarUrl = dspPluginDir + File.separator + pluginJarVo.getPluginJarName();
            File finalPluginJarFile = new File(finalPluginJarUrl);
            File dspPluginDirFile = new File(dspPluginDir);
            if (!dspPluginDirFile.exists()) {
                dspPluginDirFile.mkdirs();
            }

            //将pluginJarVo中的数据转换为pluginJar
            PluginJar pluginJar = new PluginJar();
            pluginJar.setPluginJarId(pluginJarId);
            pluginJar.setPluginJarName(pluginJarVo.getPluginJarName());
            pluginJar.setPluginJarAlias(pluginJarVo.getPluginJarAlias());
            pluginJar.setPluginJarMd5(pluginJarMd5);
            pluginJar.setPluginJarUrl(String.valueOf(finalPluginJarFile.toURI().toURL()));
            pluginJar.setPluginDefFile(pluginJarVo.getPluginDefFile());
            pluginJar.setPluginDefFileContent(pluginJarVo.getPluginDefFileContent());
            pluginJar.setDisableMark(pluginJarVo.getDisableMark() == null ? 1 : pluginJarVo.getDisableMark());
            pluginJar.setRemarkMsg(pluginJarVo.getRemarkMsg());
            pluginJar.setFileSize(pluginJarVo.getFileSize());
            pluginJar.setUploadTime(pluginJarVo.getUploadTime());
            pluginJar.setCreateTime(pluginJarVo.getCreateTime() == null ? new Date() : pluginJarVo.getCreateTime());
            pluginJar.setUpdateTime(pluginJarVo.getUpdateTime() == null ? new Date() : pluginJarVo.getUpdateTime());
            pluginJar.setPluginJarOrigin(pluginJarVo.getPluginJarOrigin().getCode());

            //查询当前用户
            User user = userService.getCurrentUser();
            pluginJar.setCreateUser(user.getUsername());

            //保存到表
            pluginJarMapper.insert(pluginJar);
            pluginMapper.insertBatch(plugins);
            //移动临时文件至plugin目录
            FileUtil.copy(pluginJarFile, finalPluginJarFile, true);
            if (!finalPluginJarFile.exists()) {
                throw DspConsoleException.generateIllegalStateException(DspResultStatus.FILE_UPLOAD_IS_FAILED);
            }
            boolean delete = pluginJarFile.delete();
//            FileUtil.delete(pluginJarFile);
        } catch (DspConsoleException e) {
            logger.error("plugin upload failed, plugin jar id: {}", pluginJarId, e);
            throw e;
        } catch (Exception e) {
            logger.error("plugin upload failed, plugin jar id: {}", pluginJarId, e);
            throw new DspConsoleException(DspResultStatus.FILE_UPLOAD_IS_FAILED);
        }
    }


    @Override
    public boolean checkIsLoaded(PluginConfig pluginConfig) {
        return true;

    }

    @Override
    public List<PluginDTO> searchByClass(Collection<String> pluginClassSet) {
        List<PluginDTO> pluginDTOS = new ArrayList<>();
        for (String pluginId : pluginClassSet) {
            PluginDTO pluginDTO = new PluginDTO();
            pluginDTO.setPluginClass(pluginId);
            pluginDTOS.add(pluginDTO);
        }
        return pluginDTOS;
    }

    @Override
    public PageWrapper<PluginJarVo> searchPlugin(PluginQuery pluginQuery) {
        //分页查询插件列表
        Page<PluginJar> page = new Page<>(pluginQuery.getPageNo(), pluginQuery.getPageSize());
        LambdaQueryChainWrapper<PluginJar> queryWrapper = getPluginJarQueryWrapper(pluginQuery);
        Page<PluginJar> pluginJarPage = queryWrapper.page(page);
        List<PluginJar> pluginJars = new ArrayList<>();
        if (pluginJarPage.getRecords() != null) {
            pluginJars = pluginJarPage.getRecords();
        }
        List<PluginJarVo> pluginJarVos = new ArrayList<>();
        final Map<String, List<PluginVo>> assemblyMap = new HashMap<>();

        List<Plugin> plugins = new ArrayList<>();
        if (pluginJars.size() > 0) {
            //根据插件jarId批量查询
            List<String> jarIds = pluginJars.stream().map(p -> p.getPluginJarId()).collect(Collectors.toList());
            plugins = new LambdaQueryChainWrapper<>(pluginMapper)
                    .in(Plugin::getPluginJarId, jarIds)
                    .list();
            if (plugins == null) plugins = new ArrayList<>();
        }
        pluginJars.forEach(pluginJar -> assemblyMap.put(pluginJar.getPluginJarId(), new ArrayList<>()));
        Map<Integer, List<FlowPluginRef>> flowRefsMap = getFlowRefsMap(plugins);
        //plugin 转换 pluginVo
        for (Plugin plugin : plugins) {
            List<PluginVo> jarPlugins = assemblyMap.get(plugin.getPluginJarId());
            if (jarPlugins == null) {
                jarPlugins = new ArrayList<>();
                assemblyMap.put(plugin.getPluginJarId(), jarPlugins);
            }
            PluginVo pluginVo = new PluginVo();
            BeanUtils.copyProperties(plugin, pluginVo);
            if (plugin.getPluginConfigs() != null) {
                pluginVo.setPluginConfigs(ObjectUtil.deSerialize(plugin.getPluginConfigs(), PluginConfigs.class));
            }
            if (plugin.getPluginInfos() != null) {
                pluginVo.setPluginInfos(ObjectUtil.deSerialize(plugin.getPluginInfos(), JSONObject.class));
            }
            pluginVo.setPluginType(PluginType.getPluginType(plugin.getPluginType()));
            List<FlowPluginRef> pluginRefs = flowRefsMap.getOrDefault(plugin.getId(), new ArrayList<>());
            pluginVo.setFlowPluginRefs(pluginRefs);
            jarPlugins.add(pluginVo);
        }

        //pluginJar 转换 pluginJarVo
        for (PluginJar pluginJar : pluginJars) {
            PluginJarVo pluginJarVo = new PluginJarVo();
            BeanUtils.copyProperties(pluginJar, pluginJarVo);
            pluginJarVo.setPluginJarOrigin(PluginJarOrigin.getPluginJarOrigin(pluginJar.getPluginJarOrigin()));
            List<PluginVo> pluginVos = assemblyMap.get(pluginJarVo.getPluginJarId());
            pluginJarVo.setPlugins(pluginVos);
            pluginJarVos.add(pluginJarVo);
        }
        return new PageWrapper<PluginJarVo>(pluginJarVos, pluginJarPage.getTotal(), pluginJarPage.getCurrent(), pluginJarPage.getPages());
    }

    /**
     * 通过pluginPk查询出flowPluginRef的list
     *
     * @param plugins
     * @return Map<Integer, List < FlowPluginRef>>
     */
    private Map<Integer, List<FlowPluginRef>> getFlowRefsMap(List<Plugin> plugins) {
        List<Integer> pluginPks = plugins.stream().map(plugin -> plugin.getId()).collect(Collectors.toList());
        LambdaQueryChainWrapper<FlowPluginRef> flowPluginRefQueryWrapper = new LambdaQueryChainWrapper<FlowPluginRef>(flowPluginRefMapper)
                .in(FlowPluginRef::getPluginPk, pluginPks);
        List<FlowPluginRef> flowPluginRefs = null;
        if (!(pluginPks == null || pluginPks.size() == 0)) {
            flowPluginRefs = flowPluginRefQueryWrapper.list();
        }
        if (flowPluginRefs == null) flowPluginRefs = new ArrayList<>();
        //创建一个map存放pluginid和对应的所有的flowref
        Map<Integer, List<FlowPluginRef>> flowRefsMap = new HashMap<>();
        //根据pluginid,把ref进行分组,并存入map中
        flowPluginRefs.forEach(flowPluginRef -> {
            List<FlowPluginRef> refs = flowRefsMap.computeIfAbsent(flowPluginRef.getPluginPk(), r -> new ArrayList<FlowPluginRef>());
            refs.add(flowPluginRef);
        });
        return flowRefsMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disablePlugin(PluginJarVo pluginJarVo) {
        CheckTool.checkIsNull(pluginJarVo.getId(), DspResultStatus.PARAM_IS_NULL);
        //查询数据库表dsp_flow_plugin
        checkPluginJarIsCited(pluginJarVo);
        //插件没有被引用, 可以去dsp_plugin_jar修改disablemark属性
        PluginJar pluginJar = findPluginJarById(pluginJarVo);
        pluginJar.setDisableMark(pluginJar.getDisableMark() == 0 ? 1 : 0);
        new LambdaUpdateChainWrapper<>(pluginJarMapper)
                .eq(PluginJar::getId, pluginJarVo.getId())
                .update(pluginJar);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlugin(PluginJarVo pluginJarVo) {
        //对参数中的id进行判空, 空则异常, 不空则删除
        Integer pluginJarId = pluginJarVo.getId();
        CheckTool.checkIsNull(pluginJarId, DspResultStatus.PARAM_IS_NULL);
        //查询数据库表dsp_flow_plugin
        checkPluginJarIsCited(pluginJarVo);
        //插件没有被引用, 可以去dsp_plugin_jar修改disablemark属性
        PluginJar pluginJar = findPluginJarById(pluginJarVo);
        //先删除plugin表中的相关记录, 才可以删jar表中的记录, 开启事务
        new LambdaUpdateChainWrapper<>(pluginMapper)
                .eq(Plugin::getPluginJarId, pluginJar.getPluginJarId())
                .remove();
        pluginJarMapper.deleteById(pluginJarId);
        //删除插件目录下的jar包
        try {
            File pluginJarFile = new File(new URI(pluginJarVo.getPluginJarUrl()));
            if (pluginJarFile.exists()) {
                pluginJarFile.delete();
            }
        } catch (Exception e) {
            logger.error("delete plugin jar failed, plugin jar id is {}", pluginJarId, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PluginJarFileVo uploadPlugin(MultipartFile file, String pluginJarId) {
        //file判空
        if (file.isEmpty()) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.FILE_UPLOAD_IS_EMPTY);
        }
        //jarId判空
        CheckTool.checkIsNull(pluginJarId, DspResultStatus.PARAM_IS_NULL);
        File pluginJarFile = null;
        String finalFileName = null;
        try {
            //生成fileName
            long time = System.currentTimeMillis();
            //判断upload还是reload
            if (pluginJarId.equals("-1")) {
                //upload,需要得到一个fileName:(文件原名+大小+时间戳)md5
                String filename = file.getOriginalFilename();
                long fileSize = file.getSize();
                String md5 = MD5Utils.md5(String.format(ConsoleConstants.PLUGIN_UPLOAD_JAR_ID_MD5_FORMAT, filename, fileSize, time));
                pluginJarId = md5;
                finalFileName = String.format(ConsoleConstants.PLUGIN_UPLOAD_JAR_NAME_FORMAT, md5, time);
            } else {
                finalFileName = String.format(ConsoleConstants.PLUGIN_UPLOAD_JAR_NAME_FORMAT, pluginJarId, time);
            }
            //指定保存目录
            String tmpDir = SystemEnvManager.getInstance().getDspTmpDir();
            String uploadDir = null;
            File uploadDirFile = null;
            EPlatform os = SystemUtil.getSystemOS();
            if (os == EPlatform.Windows) {
                uploadDir = tmpDir + File.separator + ConsoleConstants.PLUGIN_UPLOAD_WIN_TMP_DIR;
                uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                    String sets = "attrib +H \"" + uploadDirFile.getAbsolutePath() + "\"";
                    Runtime.getRuntime().exec(sets);
                }
            } else if (os == EPlatform.Linux || os == EPlatform.Mac_OS || os == EPlatform.Mac_OS_X) {
                uploadDir = tmpDir + File.separator + ConsoleConstants.PLUGIN_UPLOAD_LINUX_TMP_DIR;
                uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
            }

            String absoluteFilePath = uploadDir + File.separator + finalFileName;
            pluginJarFile = new File(absoluteFilePath);
            if (pluginJarFile.exists()) {
                pluginJarFile.delete();
            }
            pluginJarFile.createNewFile();
            file.transferTo(pluginJarFile);
        } catch (Exception e) {
            logger.error("plugin upload is failed, upload temp file name is {}", finalFileName, e);
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.FILE_UPLOAD_IS_FAILED);
        }
        PluginJarFileVo res = new PluginJarFileVo();
        res.setStatus("done");
        res.setResponse("file upload successfully");
        res.setUrl(pluginJarFile.getAbsolutePath());
        res.setPluginJarId(pluginJarId);
        return res;
    }

    @Override
    public PluginJarVo verifyPlugin(PluginJarFileVo fileVo) {
        CheckTool.checkIsNull(fileVo, DspResultStatus.PARAM_ERROR);
        if (!fileVo.getStatus().equalsIgnoreCase("done")) {
            throw new DspConsoleException(DspResultStatus.FILE_UPLOAD_IS_BROKEN, "The file is broken, please check and upload again.");
        }
        if (StringUtils.isBlank(fileVo.getName())) {
            throw new DspConsoleException(DspResultStatus.PARAM_IS_NULL, "The file name is blank, please check and upload again.");
        }
        if (StringUtils.isBlank(fileVo.getPluginJarId())) {
            throw new DspConsoleException(DspResultStatus.PARAM_IS_NULL, "Failed to generate plugin jar id, please upload again.");
        }
        if (StringUtils.isBlank(fileVo.getUrl())) {
            throw new DspConsoleException(DspResultStatus.PARAM_IS_NULL, "Failed to find plugin jar url, please upload again.");
        } else {
            File file = new File(fileVo.getUrl());
            if (!file.exists()) {
                throw new DspConsoleException(DspResultStatus.FILE_UPLOAD_IS_BROKEN, "The file uploaded is broken, please upload again.");
            }
        }
        // 1. 通过url读取临时jar文件
        File tmpJarFile = new File(fileVo.getUrl());
        URL pluginJarUrl = null;
        final PluginClassLoader pluginClassLoader = new PluginClassLoader();
        try {
            // 2. 加载jar文件
            pluginJarUrl = tmpJarFile.toURI().toURL();
            pluginClassLoader.loadJarFile(pluginJarUrl);
            JarScanner scanner = new JarScanner(pluginClassLoader, pluginJarUrl);
            // 3. 扫描jar文件
            List<URL> metaFiles = scanner.scanFiles("Dsp-Plugin", ".json");
            if (metaFiles.size() != 1) {
                logger.error("Failed to resolve plugin Jar for more than one definition files prefixed with \"Dsp-Plugin\" are found. Only one def file is required. Plugin jar id: {}", fileVo.getPluginJarId());
                throw new DspConsoleException(DspResultStatus.PLUGIN_DEF_FILE_SHOULD_BE_UNIQUE, "Failed to resolve plugin Jar for more than one definition files prefixed with \"Dsp-Plugin\" are found. Only one def file is required. Plugin jar name: " + fileVo.getName());
            }
            // 4. 解析jar文件中的pluginJson文件
            URL metaFileUrl = metaFiles.get(0);
            File defFile = new File(String.valueOf(metaFileUrl));
            String pluginDefFileContent = FileUtil.readFileContentFromJar(metaFileUrl);
            JSONArray pluginJsons = null;
            try {
                pluginJsons = ObjectUtil.contentToObject(pluginDefFileContent, JSONArray.class);
            } catch (JsonProcessingException e) {
                logger.error("Failed to transfer definition file content to JSONArray: ", e);
                throw new DspConsoleException(DspResultStatus.PLUGIN_DEF_FILE_TRANSFER_IS_FAILED, "Failed to transfer definition file content to JSONArray. Please check and upload again. Plugin jar name: " + fileVo.getName());
            }
            // 5. 将解析出的pluginJson转换为PluginVo对象
            List<PluginVo> pluginVos = new ArrayList<>();
            List<String> newPluginNames = new ArrayList<>();
            Map<String, PluginConfig> newPluginClassMap = new HashMap<>();
            for (Object pluginJson : pluginJsons) {
                JSONObject object = new JSONObject((Map<String, Object>) pluginJson);
                PluginConfig pluginConfig = ObjectUtil.mapToBean(object, PluginConfig.class);
                PluginVo pluginVo = new PluginVo();
                pluginVo.setPluginName(pluginConfig.getPluginName());
                pluginVo.setPluginType(pluginConfig.getPluginType());
                pluginVo.setPluginClass(pluginConfig.getPluginClass());
                pluginVo.setPluginDescription(pluginConfig.getPluginDescription());
                pluginVo.setPluginConfigs(pluginConfig.getPluginConfigs());
                pluginVo.setPluginInfos(pluginConfig.getPluginInfos());
                pluginVos.add(pluginVo);
                // 4.1 校验pluginClass的合法性, 即class是否能够被加载
                try {
                    pluginClassLoader.loadClass(pluginVo.getPluginClass());
                    if (log.isDebugEnabled())
                        logger.debug("verify plugin information: plugin name: {}, plugin class: {}", pluginConfig.getPluginName(), pluginConfig.getPluginClass());
                } catch (ClassNotFoundException e) {
                    throw new DspConsoleException(DspResultStatus.PLUGIN_CLASS_LOAD_FAILED, "Failed to load class from plugin jar. Please check and upload again. Plugin class: " + pluginConfig.getPluginClass());
                }
                newPluginNames.add(pluginConfig.getPluginName());
                newPluginClassMap.put(pluginConfig.getPluginClass(), pluginConfig);
            }

            // 5. 判断upload还是reload, 如果表中存在pluginJarId为reload, 不存在为upload
            String pluginJarId = fileVo.getPluginJarId();
            PluginJar pluginJarCheck = new LambdaQueryChainWrapper<PluginJar>(pluginJarMapper)
                    .eq(PluginJar::getPluginJarId, pluginJarId)
                    .one();
            // true为upload, false为reload
            boolean isUpload = pluginJarCheck == null ? true : false;

            // 6. 校验
            // 6.1 公共校验规则: 不可以上传一模一样的插件, 用md5判断
            String pluginJarMd5;
            try (FileInputStream fileInputStream = new FileInputStream(tmpJarFile)) {
                pluginJarMd5 = DigestUtils.md5Hex(fileInputStream);
            }
            // 校验插件reupload的是完全相同的文件
            PluginJar oldJar = new LambdaQueryChainWrapper<>(pluginJarMapper)
                    .eq(PluginJar::getPluginJarMd5, pluginJarMd5)
                    .one();
            if (oldJar != null && oldJar.getPluginJarMd5().equalsIgnoreCase(pluginJarMd5)) {
                logger.info("Plugin jar already exists. plugin jar id: {}, plugin jar name: {}, plugin jar md5: {}", pluginJarId, fileVo.getName(), pluginJarMd5);
                throw new DspConsoleException(DspResultStatus.PLUGIN_JAR_EXISTS, "Plugin jar already exists. Plugin jar name: " +  oldJar.getPluginJarName());
            }

            // 6.2 分别对上传和更新做校验
            if (isUpload) {
                // 上传规则: 不可以上传重复的插件名称和class
                List<Plugin> plugins = new LambdaQueryChainWrapper<Plugin>(pluginMapper)
                        .in(Plugin::getPluginName, newPluginNames)
                        .or()
                        .in(Plugin::getPluginClass, newPluginClassMap.keySet())
                        .list();
                if (plugins != null && plugins.size() > 0) {
                    logger.error("There is a conflict between the plugin name or class and the existing plugin, please check the detailed log and upload again");
                    throw new DspConsoleException(DspResultStatus.PLUGIN_EXISTS, "Plugin already exists! Please check and reload. Plugin jar name: " + fileVo.getName());
                }
            } else {
                // 更新规则 1. 新上传的jar包中的class不能存在于库中其他jar包中
                // 将库中的class分为本次更新jar的plugin和其他plugin, 从而可判断新上传的class是否存在于其他plugin中
                List<Plugin> dbPlugins = new LambdaQueryChainWrapper<>(pluginMapper)
                        .list();
                Map<String, Plugin> otherClassMap = new HashMap<>();
                for (Plugin plugin : dbPlugins) {
                    if (!pluginJarId.equals(plugin.getPluginJarId())) {
                        otherClassMap.put(plugin.getPluginClass(), plugin);
                    }
                }
                for (String newClass : newPluginClassMap.keySet()) {
                    Plugin plugin = otherClassMap.get(newClass);
                    if (plugin != null) {
                        logger.error("There is a conflict between the plugin class and the existing plugin, please check the detailed log and re-upload");
                        throw new DspConsoleException(DspResultStatus.PLUGIN_EXISTS, "Plugin already exists! Please check and reload. Plugin name: " + plugin.getPluginName());
                    }
                }
                // 更新规则 2. 被流关联的插件不可以删除
                // 使用pluginJarId查询dsp_flow_plugin中的相关记录
                Map<String, FlowPluginRef> classFlowRefMap = flowPluginRefMapper.selectPluginClassMapByJarId(pluginJarId);
                // 如果classFlowRefMap不为空, 则遍历flowMap, 如果flowMap中的class在新上传的jar中找不到, 就报错
                if (classFlowRefMap != null && classFlowRefMap.size() > 0) {
                    for (String pluginClass : classFlowRefMap.keySet()) {
                        PluginConfig pluginConfig = newPluginClassMap.get(pluginClass);
                        if (pluginConfig == null) {
                            logger.error("Plugin is cited by flow, please release plugin first. Plugin class: {}", pluginClass);
                            throw new DspConsoleException(DspResultStatus.PLUGIN_IS_CITED, "Plugin is cited by flow, please release plugin first. Plugin class: " + pluginClass);
                        }
                    }
                }
            }
            // 组装pluginJarVo
            PluginJarVo pluginJarVo = new PluginJarVo();
            pluginJarVo.setPluginJarId(pluginJarId);
            pluginJarVo.setPluginJarMd5(pluginJarMd5);
            pluginJarVo.setPluginJarName(fileVo.getName());
            pluginJarVo.setPluginJarUrl(pluginJarUrl.toString());
            pluginJarVo.setPluginDefFile(defFile.getName());
            pluginJarVo.setPluginDefFileContent(pluginDefFileContent);
            pluginJarVo.setDisableMark(0);
            pluginJarVo.setFileSize(tmpJarFile.length());
            BasicFileAttributes attr = FileUtil.getFileAttributes(tmpJarFile);
            long pluginJarUploadTime = attr.lastModifiedTime().toMillis();
            pluginJarVo.setUploadTime(new Date(pluginJarUploadTime));
            pluginJarVo.setPlugins(pluginVos);
            pluginJarVo.setPluginJarOrigin(PluginJarOrigin.USER);
            logger.info("Verify plugin successfully, plugin jar id: {}, plugin jar md5: {}", pluginJarVo.getPluginJarId(), pluginJarVo.getPluginJarMd5());
            return pluginJarVo;
        } catch (DspConsoleException e) {
            logger.error("Failed to verify plugin, plugin jar id: {}", fileVo.getPluginJarId(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Failed to verify plugin, plugin jar id: {}", fileVo.getPluginJarId(), e);
            throw new DspConsoleException(DspResultStatus.PLUGIN_JAR_VERIFY_FAILED, "Failed to verify plugin, please check and upload again. Plugin jar id: " + fileVo.getPluginJarId());
        } finally {
            if (pluginJarUrl != null) {
                pluginClassLoader.unloadJarFile(pluginJarUrl);
                try {
                    pluginClassLoader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePart(PluginJarVo pluginJarVo) {
        CheckTool.checkIsNull(pluginJarVo, DspResultStatus.PARAM_IS_NULL);
        if (StringUtils.isBlank(pluginJarVo.getPluginJarId())) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PARAM_IS_NULL);
        }
        // 更新plugin_jar
        String pluginJarAlias = pluginJarVo.getPluginJarAlias();
        String remarkMsg = pluginJarVo.getRemarkMsg();
        LambdaUpdateChainWrapper<PluginJar> updateWrapper = new LambdaUpdateChainWrapper<>(pluginJarMapper);
        if (StringUtils.isNotBlank(pluginJarVo.getPluginJarId())) {
            updateWrapper.eq(PluginJar::getPluginJarId, pluginJarVo.getPluginJarId());
        }
        if (StringUtils.isNotBlank(pluginJarAlias)) {
            updateWrapper.set(PluginJar::getPluginJarAlias, pluginJarAlias);
        }
        if (StringUtils.isNotBlank(remarkMsg)) {
            updateWrapper.set(PluginJar::getRemarkMsg, remarkMsg);
        }
        updateWrapper.update();

        // 更新plugin
        List<PluginVo> plugins = pluginJarVo.getPlugins();

        for (PluginVo plugin : plugins) {
            LambdaUpdateChainWrapper<Plugin> pluginUpdateWrapper = new LambdaUpdateChainWrapper<>(pluginMapper);
            if(StringUtils.isNotBlank(plugin.getPluginId())){
                pluginUpdateWrapper.eq(Plugin::getPluginId, plugin.getPluginId());
            }

            if(StringUtils.isNotBlank(plugin.getPluginAlias())){
                pluginUpdateWrapper.set(Plugin::getPluginAlias, plugin.getPluginAlias());
            } else {
                pluginUpdateWrapper.set(Plugin::getPluginAlias, null);
            }

            if(StringUtils.isNotBlank(plugin.getPluginDescription())){
                pluginUpdateWrapper.set(Plugin::getPluginDescription, plugin.getPluginDescription());
            } else {
                pluginUpdateWrapper.set(Plugin::getPluginDescription, null);
            }
            pluginUpdateWrapper.update();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlugin(PluginJarVo pluginJarVo) {
        //检查pluginJarId不为空
        String pluginJarId = pluginJarVo.getPluginJarId();
        if (StringUtils.isBlank(pluginJarId)) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PARAM_IS_NULL);
        }
        //检查jar文件是否上传到临时目录, 找不到则报错
        File pluginJarFile = null;
        try {
            pluginJarFile = new File(new URI(pluginJarVo.getPluginJarUrl()));
        } catch (URISyntaxException e) {
            logger.error("plugin jar url failed to transfer to uri, plugin jar url: {}", pluginJarVo.getPluginJarUrl(), e);
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PLUGIN_UPDATE_IS_FAILED);
        }
        if (!pluginJarFile.exists()) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PLUGIN_JAR_FILE_NOT_EXISTS);
        }
        //准备最终目录的url
        String dspPluginDir = SystemEnvManager.getInstance().getDspPluginDir();
        File dspPluginDirFile = new File(dspPluginDir);
        if (!dspPluginDirFile.exists()) {
            dspPluginDirFile.mkdirs();
        }
        String finalPluginJarUrl = dspPluginDir + File.separator + pluginJarVo.getPluginJarName();
        File finalPluginJarFile = new File(finalPluginJarUrl);
        //操作plugin表,基于class修改,通过pluginJarId获取数据库中的plugin
        Map<String, Plugin> oldPlugins = pluginMapper.selectMapByJarId(pluginJarId);
        Map<String, Plugin> newPlugins = new HashMap<>();
        List<PluginVo> pluginVos = pluginJarVo.getPlugins();
        try {
            //获取新插件的class和Plugin对象
            for (PluginVo pluginVo : pluginVos) {
                Plugin plugin = new Plugin();
                plugin.setPluginJarId(pluginJarId);
                plugin.setPluginName(pluginVo.getPluginName());
                plugin.setPluginAlias(pluginVo.getPluginAlias());
                plugin.setPluginClass(pluginVo.getPluginClass());
                plugin.setPluginType(pluginVo.getPluginType().getCode());
                plugin.setPluginDescription(pluginVo.getPluginDescription());
                plugin.setPluginConfigs(ObjectUtil.objectToContent(pluginVo.getPluginConfigs()));
                plugin.setPluginInfos(ObjectUtil.objectToContent(pluginVo.getPluginInfos()));
                plugin.setUpdateTime(new Date());
                newPlugins.put(pluginVo.getPluginClass(), plugin);
            }
            Map<String, Plugin> updateMap = new HashMap<>();
            List<Plugin> addPlugin = new ArrayList<>();
            List<String> deleteClasses = new ArrayList<>();
            for (String newClass : newPlugins.keySet()) {
                Plugin newPlugin = newPlugins.get(newClass);
                Plugin oldPlugin = oldPlugins.get(newClass);
                if (oldPlugin == null) {
                    String pluginId = MD5Utils.md5(String.format(ConsoleConstants.PLUGIN_ID_MD5_FORMAT, newPlugin.getPluginName(), newPlugin.getPluginClass()));
                    newPlugin.setPluginId(pluginId);
                    addPlugin.add(newPlugin);
                } else {
                    newPlugin.setPluginId(oldPlugin.getPluginId());
                    updateMap.put(newClass, newPlugin);
                }
            }
            for (String oldClass : oldPlugins.keySet()) {
                Plugin plugin = updateMap.get(oldClass);
                if (plugin == null) {
                    deleteClasses.add(oldClass);
                }
            }
            //操作plugin表
            if (deleteClasses != null && deleteClasses.size() > 0) {
                pluginMapper.deleteBatchByClass(deleteClasses);
            }
            if (updateMap != null && updateMap.size() > 0) {
                pluginMapper.updateBatchByClass(updateMap.values());
            }
            if (addPlugin != null && addPlugin.size() > 0) {
                pluginMapper.insertBatch(addPlugin);
            }
            //操作pluginJar表
            //将pluginJarVo中的数据转换为pluginJar
            PluginJar pluginJar = new PluginJar();
            pluginJar.setPluginJarId(pluginJarId);
            pluginJar.setPluginJarName(pluginJarVo.getPluginJarName());
            pluginJar.setPluginJarAlias(pluginJarVo.getPluginJarAlias());
            pluginJar.setPluginJarMd5(pluginJarVo.getPluginJarMd5());
            pluginJar.setPluginJarUrl(String.valueOf(finalPluginJarFile.toURI().toURL()));
            pluginJar.setPluginDefFile(pluginJarVo.getPluginDefFile());
            pluginJar.setPluginDefFileContent(pluginJarVo.getPluginDefFileContent());
            pluginJar.setDisableMark(pluginJarVo.getDisableMark() == null ? 1 : pluginJarVo.getDisableMark());
            pluginJar.setRemarkMsg(pluginJarVo.getRemarkMsg());
            pluginJar.setFileSize(pluginJarVo.getFileSize());
            pluginJar.setCreateUser(userService.getCurrentUser().getUsername());
            pluginJar.setUploadTime(new Date());
            pluginJar.setUpdateTime(new Date());
            new LambdaUpdateChainWrapper<PluginJar>(pluginJarMapper)
                    .eq(PluginJar::getPluginJarId, pluginJarId)
                    .update(pluginJar);
            //查询关联的flow, 设置关联flow的updateMark为1
            List<Integer> flowPks = flowPluginRefMapper.selectFlowPksByJarId(pluginJarId);
            if (flowPks.size() > 0) {
                new LambdaUpdateChainWrapper<>(flowMapper)
                        .in(Flow::getId, flowPks)
                        .set(Flow::getUpdateMark, 1)
                        .update();
            }
            //移动临时文件至plugin目录
            FileUtil.copy(pluginJarFile, finalPluginJarFile, true);
            if (!finalPluginJarFile.exists()) {
                throw DspConsoleException.generateIllegalStateException(DspResultStatus.FILE_UPDATE_IS_FAILED);
            }
            FileUtil.delete(pluginJarFile);
        } catch (Exception e) {
            logger.error("plugin update failed, plugin jar id: {}", pluginJarId, e);
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PLUGIN_UPDATE_IS_FAILED);
        }
    }

    /**
     * 查询所有的plugin, 每个type封装成一个list, 存入PluginListVo中
     *
     * @return
     */
    @Override
    public PluginListVo searchAll() {
        List<Plugin> plugins = new LambdaQueryChainWrapper<>(pluginMapper)
                .list();
        PluginListVo pluginListVo = new PluginListVo();
        if (plugins == null) {
            return pluginListVo;
        }
        List<PluginVo> inputPluginVos = new ArrayList<>();
        List<PluginVo> outputPluginVos = new ArrayList<>();
        List<PluginVo> processPluginVos = new ArrayList<>();
        List<PluginVo> splitPluginVos = new ArrayList<>();
        List<PluginVo> unionPluginVos = new ArrayList<>();
        List<PluginVo> systemPluginVos = new ArrayList<>();
        List<PluginVo> unknownPluginVos = new ArrayList<>();

        Map<String, Object> enableJarMap = pluginJarMapper.selectEnableJarMap();
        for (Plugin plugin : plugins) {
            PluginType pluginType = PluginType.getPluginType(plugin.getPluginType());
            PluginVo pluginVo = PluginVo.fromPlugin(plugin);
            Object o = enableJarMap.get(plugin.getPluginJarId());
            if (o == null) continue;
            switch (pluginType) {
                case INPUT:
                    inputPluginVos.add(pluginVo);
                    break;
                case OUTPUT:
                    outputPluginVos.add(pluginVo);
                    break;
                case PROCESS:
                    processPluginVos.add(pluginVo);
                    break;
                case SPLIT:
                    splitPluginVos.add(pluginVo);
                    break;
                case UNION:
                    unionPluginVos.add(pluginVo);
                    break;
                case SYSTEM:
                    systemPluginVos.add(pluginVo);
                    break;
                case UNKNOWN:
                    unknownPluginVos.add(pluginVo);
                    break;
                default:
                    break;
            }
        }

        pluginListVo.setInputPlugins(inputPluginVos);
        pluginListVo.setOutputPlugins(outputPluginVos);
        pluginListVo.setProcessPlugins(processPluginVos);
        pluginListVo.setSplitPlugins(splitPluginVos);
        pluginListVo.setUnionPlugins(unionPluginVos);
        pluginListVo.setSystemPlugins(systemPluginVos);
        pluginListVo.setUnknownPlugins(unknownPluginVos);
        return pluginListVo;
    }

    public List<Plugin> findPluginByPluginIds(Set<String> pluginIds) {
        LambdaQueryChainWrapper<Plugin> pluginWrapper = new LambdaQueryChainWrapper<>(pluginMapper);
        pluginWrapper.in(Plugin::getPluginId, pluginIds);
        return pluginWrapper.list();
    }

    public Map<String, PluginJar> findPluginJarMapByJarIds(Set<String> pluginJarIds) {
        Map<String, PluginJar> pluginJarMap = new HashMap<>();
        pluginJarMap = pluginJarMapper.selectJarMapByJarIds(pluginJarIds);
        return pluginJarMap;
    }

    @Override
    public Map<String, PluginDTO> findPluginMapByIds(Set<String> pluginIds) {
        if (pluginIds == null || pluginIds.size() == 0) return null;
        Map<String, Plugin> pluginMap = pluginMapper.selectMapByIds(pluginIds);
        Map<String, PluginDTO> pluginDTOMap = new HashMap<>();
        for (Plugin plugin : pluginMap.values()) {
            PluginDTO pluginDTO = PluginDTO.fromPlugin(plugin);
            pluginDTOMap.put(plugin.getPluginId(), pluginDTO);
        }
        return pluginDTOMap;
    }

    @Override
    public Map<String, PluginJar> findPluginJarNameMap() {
        Map<String, PluginJar> pluginJarNameMap = pluginJarMapper.selectPluginJarNameMap();
        return pluginJarNameMap;
    }

    /**
     * 获取插件class映射表
     *
     * @return Map<String, Plugin> key为pluginClass, value为Plugin实例
     */
    @Override
    public Map<String, Plugin> findPluginClassMap() {
        Map<String, Plugin> pluginClassMap = pluginMapper.selectPluginClassMap();
        return pluginClassMap;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createJarsAndPlugins(List<PluginJar> newPluginJars, List<Plugin> newPlugins) {
        if (newPluginJars != null && newPluginJars.size() > 0) {
            pluginJarMapper.insertBatch(newPluginJars);
        }

        if (newPlugins != null && newPlugins.size() > 0) {
            pluginMapper.insertBatch(newPlugins);
        }
    }

    private PluginJar findPluginJarById(PluginJarVo pluginJarVo) {
        LambdaQueryChainWrapper<PluginJar> pluginJarWrapper = new LambdaQueryChainWrapper<PluginJar>(pluginJarMapper);
        pluginJarWrapper.eq(PluginJar::getId, pluginJarVo.getId());
        return pluginJarWrapper.one();
    }

    private void checkPluginJarIsCited(PluginJarVo pluginJarVo) {
        LambdaQueryChainWrapper<FlowPluginRef> wrapper = new LambdaQueryChainWrapper<FlowPluginRef>(flowPluginRefMapper);
        wrapper.eq(FlowPluginRef::getPluginJarPk, pluginJarVo.getId());
        List<FlowPluginRef> list = wrapper.list();
        CheckTool.checkState(list.size() > 0, DspResultStatus.PLUGIN_IS_CITED);
    }

    private LambdaQueryChainWrapper<PluginJar> getPluginJarQueryWrapper(PluginQuery pluginQuery) {
        LambdaQueryChainWrapper<PluginJar> wrapper = new LambdaQueryChainWrapper<PluginJar>(pluginJarMapper);
        if (StringUtils.isNotBlank(pluginQuery.getPluginJarId())) {
            wrapper.eq(PluginJar::getPluginJarId, pluginQuery.getPluginJarId());
        }
        if (StringUtils.isNotBlank(pluginQuery.getPluginJarAlias())) {
            wrapper.eq(PluginJar::getPluginJarAlias, pluginQuery.getPluginJarAlias());
        }
        if (StringUtils.isNotBlank(pluginQuery.getPluginJarName())) {
            wrapper.eq(PluginJar::getPluginJarName, pluginQuery.getPluginJarName());
        }
        if (pluginQuery.getId() != null) {
            wrapper.eq(PluginJar::getId, pluginQuery.getId());
        }
        if (pluginQuery.getDisableMark() != null) {
            wrapper.eq(PluginJar::getDisableMark, pluginQuery.getDisableMark());
        }
        if (pluginQuery.getPluginJarOrigin() != null) {
            wrapper.eq(PluginJar::getPluginJarOrigin, pluginQuery.getPluginJarOrigin());
        }
        String sortField = pluginQuery.getSortField();
        PageQuery.SortOrder sortOrder = pluginQuery.getSortOrder();
        if (StringUtils.isNotBlank(sortField)) {
            if (sortOrder == PageQuery.SortOrder.descend) {
                wrapper.orderByDesc(PluginJar::getCreateTime);
            } else {
                wrapper.orderByAsc(PluginJar::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(PluginJar::getCreateTime);
        }
        return wrapper;
    }

    private Plugin findPluginByClass(String pluginClass) {
        LambdaQueryChainWrapper<Plugin> queryWrapper = new LambdaQueryChainWrapper<>(pluginMapper);
        queryWrapper.eq(Plugin::getPluginClass, pluginClass);
        return pluginMapper.selectOne(queryWrapper);
    }
}
