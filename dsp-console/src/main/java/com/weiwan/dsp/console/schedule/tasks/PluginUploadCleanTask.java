package com.weiwan.dsp.console.schedule.tasks;

import com.weiwan.dsp.common.enums.EPlatform;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.SystemUtil;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.schedule.ConsoleTaskContext;
import com.weiwan.dsp.console.schedule.task.DataCleanTask;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/5 23:06
 * @Package: com.weiwan.dsp.console.schedule.tasks
 * @ClassName: PluginUploadCleanTask
 * @Description:
 **/
public class PluginUploadCleanTask extends DataCleanTask {

    private static final Logger logger = LoggerFactory.getLogger(PluginUploadCleanTask.class);

    @Override
    protected void executeInternal(ConsoleTaskContext context) {
        logger.info("Start cleaning expired plugin temp files");
        try {
            //从context中的taskConfigs中获取配置的插件的保留时间
            long retentionTimeSeconds = context.getTaskConfigs().getLongVal("retentionTimeSeconds", 86400);
            //获取项目临时目录
            String tmpDir = SystemEnvManager.getInstance().getDspTmpDir();
            //获取当前操作系统
            EPlatform os = SystemUtil.getSystemOS();
            //不同操作系统存放插件的临时目录可能不一致, 根据不同os获取目录
            if (os == EPlatform.Windows) {
                tmpDir = tmpDir + File.separator + ConsoleConstants.PLUGIN_UPLOAD_WIN_TMP_DIR;
            } else if (os == EPlatform.Linux || os == EPlatform.Mac_OS || os == EPlatform.Mac_OS_X) {
                tmpDir = tmpDir + File.separator + ConsoleConstants.PLUGIN_UPLOAD_LINUX_TMP_DIR;
            }
            File tmpDirFile = new File(tmpDir);
            if (tmpDirFile.exists()) {
                //获取目录下的所有文件
                File[] jarFiles = tmpDirFile.listFiles();
                //遍历文件, 分割出文件名上的时间戳, 判断是否超过24小时, 超过则删除文件
                if (jarFiles != null && jarFiles.length > 0) {
                    for (File jarFile : jarFiles) {
                        String fileName = jarFile.getName();
                        if(!fileName.contains("_")) continue;
                        String[] splitName = fileName.split("_|[.]");
                        String fileTimeStamp = splitName[1];
                        long fileCreateTime = Long.parseLong(fileTimeStamp);
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - fileCreateTime > retentionTimeSeconds * 1000) {
                            FileUtil.delete(jarFile);
                        }
                    }
                }
            }
            logger.info("Finished cleaning expired plugin temp files");
        } catch (Exception e) {
            logger.error("error cleaning plugin temporary files", e);
        }
    }
}
