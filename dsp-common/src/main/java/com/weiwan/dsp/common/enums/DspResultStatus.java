package com.weiwan.dsp.common.enums;

/**
 * @author: xiaozhennan1995@gmail.com
 */
public enum DspResultStatus implements ResultStatus {

    OK(1000, "成功"),
    PARAM_ERROR(1001, "参数非法"),
    LOGIN_ERROR(1002, "登录失败"),
    INTERNAL_SERVER_ERROR(1003, "服务异常"),
    RECORD_NOT_EXIST(1005, "记录不存在"),
    FAILED_DEL_OWN(1006, "不能删除自己"),
    FAILED_USER_ALREADY_EXIST(1007, "该用户已存在"),
    FAILED_LOCK_OWN(1008, "不能禁用自己"),
    UNAUTHORIZED(1009, "未授权"),
    PARAM_IS_NULL(1010, "参数为空"),
    APPLICATION_TYPE_NULL(2000, "应用类型为空"),
    APPLICATION_NAME_NULL(2001, "应用名称为空"),
    APPLICATION_FLOW_ID_NULL(2002, "流程ID为空, 一个应用必须关联一个流程"),
    APPLICATION_FLOW_NOT_EXIST(2003, "应用流程不存在"),
    APPLICATION_CORE_CONFIG_NOT_EXIST(2004, "核心配置不存在"),
    APPLICATION_ENGINE_CONFIG_NOT_EXIST(2005, "应用引擎配置不存在"),
    APPLICATION_APP_CONFIG_NOT_EXIST(2006, "核心应用配置不存在"),
    APPLICATION_NAME_EXIST(2007, "应用名称相同, 已经存在相同的应用了"),
    APPLICATION_DEPLOY_RUNNING(2008, "应用正在运行, 需要先停止应用部署"),
    APPLICATION_DEPLOY_DELETE_ERROR(2009, "删除应用部署失败, 请检查当前应用状态"),
    APPLICATION_APP_DEPLOY_EXISTS(2010, "无法创建应用部署, 当前应用部署已经存在"),
    FLOW_DISABLED(2011, "关联的流程处于被禁用状态"),
    FAILED_ADD_FLOW_REF(2012, "无法添加流程关联,已经存在了"),
    UPDATE_DEPLOY_CONFIG_FAIL(2023, "更新作业部署配置失败"),
    APPLICATION_DEPLOY_CREATE_ERROR(2024, "应用部署创建失败, 请检查日志"),
    APPLICATION_APP_DEPLOY_NOT_EXIST(2025, "无法找到应用部署, 请重新创建应用"),
    FLOW_NODES_IS_NULL(2026, "流程节点不可以为空"),
    PLUGIN_CLASS_IS_NULL(2027, "插件属性错误, ClassName不能为空"),
    PLUGIN_IS_NOT_FOUNT(2028, "无法找到插件, 请检查插件是否存在"),
    PLUGIN_IS_DISABLE(2029, "插件被禁用, 无法使用"),
    PLUGIN_NOT_LOAD(2030, "插件没有被加载, 请检查插件状态"),
    PLUGIN_IS_REQUIRED(2031, "必须添加至少一个插件"),
    FLOW_EXIST_NAME(2032, "流程名称已经存在"),
    FLOW_IS_NOT_EXIST(2033, "流程不存在"),
    FLOW_IS_CITED(2034, "流程被引用中, 请先删除引用应用"),
    PLUGIN_NAME_IS_NULL(2035, "插件属性错误, Name不能为空"),
    PLUGIN_EXISTS(2036, "插件已经存在"),
    PLUGIN_IS_CITED(2037, "插件被引用中, 请先删除引用流程"),
    FILE_UPLOAD_IS_EMPTY(2038, "上传失败, 上传的文件不能为空"),
    FILE_UPLOAD_IS_FAILED(2039, "文件上传失败, 请重试"),
    FILE_UPLOAD_IS_BROKEN(2040, "文件已损坏, 请重新上传"),
    PLUGIN_DEF_FILE_SHOULD_BE_UNIQUE(2041, "插件定义文件必须是唯一的"),
    PLUGIN_DEF_FILE_READ_IS_FAILED(2042, "插件定义文件读取失败, 请重新上传"),
    PLUGIN_DEF_FILE_TRANSFER_IS_FAILED(2043, "插件定义文件转换失败, 请重新上传"),
    PLUGIN_DEF_FILE_NOT_FOUND(2044, "找不到插件定义文件"),
    PLUGIN_DEF_FILE_LOAD_IS_FAILED(2045, "插件定义文件加载失败, 请重新上传"),
    PLUGIN_JAR_FILE_NOT_EXISTS(2046, "文件已损坏, 请重新上传"),
    PLUGIN_JAR_EXISTS(2047, "插件包已存在"),
    PLUGIN_JAR_VERIFY_FAILED(2048, "插件包校验失败, 请重新上传"),
    PLUGIN_CLASS_LOAD_FAILED(2049, "无法加载插件Class, 请检查并重新上传"),
    PLUGIN_UPDATE_IS_FAILED(2050, "插件更新失败, 请重试"),
    FILE_UPDATE_IS_FAILED(2051, "文件更新失败, 请重试"),
    CONFIG_EXISTS(2052, "配置已存在"),
    TASK_NOT_EXIST(2053, "任务不存在"),
    TASK_EXISTS(2054, "任务已存在"),
    ILLEGAL_TASK_CLASS_NAME(2055, "非法的定时任务全类名"),
    INCONSISTENT_TASK_BEAN_NAME(2056, "定时任务类名不一致"),
    TASK_START_FAILED(2057, "定时任务启动失败, 请重试"),
    TASK_STOP_FAILED(2058, "定时任务停止失败, 请重试"),
    TASK_RESUME_FAILED(2059, "定时任务恢复失败, 请重试"),
    TASK_LOAD_FAILED(2060, "定时任务加载失败, 请重试"),
    TASK_UNLOAD_FAILED(2061, "定时任务卸载失败, 请重试"),
    TASK_RELOAD_FAILED(2062, "定时任务重载失败, 请重试"),
    TASK_ALREADY_LOADED(2063, "定时任务已被加载"),
    TASK_IS_NOT_LOADED(2064, "定时任务未加载"),
    TASK_EXECUTE_FAILED(2065, "定时任务执行失败"),
    FLOW_DOWNLOAD_FAILED(2066, "流下载失败, 请重试"),
    APPLICATION_DELETE_FAILED(2067, "应用删除失败, 请重试"),
    FLOW_EXISTS(2068, "流已存在"),
    APPLICATION_DEPLOY_NOT_RUNNING(2069, "作业不在运行状态"),
    APPLICATION_NOT_EXIST(2070, "应用不存在");

    private int code;
    private String message;

    DspResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
