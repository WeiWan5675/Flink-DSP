package com.weiwan.dsp.console.model.constant;

/**
 * 常量定义
 *
 * @author: xiaozhennan
 */
public class ConsoleConstants {

    /**
     * 菜单根id
     */
    public static final long PERMISSION_ROOT_ID = 0L;
    /**
     * 新建用户使用的默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";
    /**
     * 默认头像
     */
    public static final String DEFAULT_AVATAR = "/avatar.jpg";


    public static final String PLUGIN_UPLOAD_JAR_NAME_FORMAT = "%s_%s.jar";

    public static final String PLUGIN_UPLOAD_JAR_ID_MD5_FORMAT = "%s-%s-%s";

    public static final String PLUGIN_UPLOAD_WIN_TMP_DIR = "upload";
    public static final String PLUGIN_UPLOAD_LINUX_TMP_DIR = ".upload";

    public static final String JOB_KEY = "JOB_KEY";
    public static final String USER_TASK_METHOD_NAME = "execute";

    public static final String FLOW_EXPORT_FILE_NAME_FORMAT = "%s.flow";

    public static final String PLUGIN_ID_MD5_FORMAT = "%s-%s";
    public static String SCHEDULE_USER_CUSTOM_TASK_CLASS = "com.weiwan.dsp.console.schedule.task.UserCustomTask";

    /**
     * 构造函数私有化，避免被实例化
     */
    private ConsoleConstants() {
    }

}
