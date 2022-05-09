/*
 Navicat Premium Data Transfer

 Source Server         : flink-dsp
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : rm-2ze8v9g2o013w44f8wo.mysql.rds.aliyuncs.com:3306
 Source Schema         : flink_dsp

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 09/05/2022 15:40:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dsp_application
-- ----------------------------
DROP TABLE IF EXISTS `dsp_application`;
CREATE TABLE `dsp_application`  (
                                    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
                                    `job_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '作业ID',
                                    `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用名称',
                                    `app_type` int(11) NULL DEFAULT NULL COMMENT '应用类型',
                                    `app_state` int(11) NULL DEFAULT NULL COMMENT '应用状态',
                                    `deploy_id` int(11) NULL DEFAULT NULL COMMENT '部署关联ID',
                                    `engine_type` int(11) NULL DEFAULT NULL COMMENT '引擎类型',
                                    `engine_mode` int(11) NULL DEFAULT NULL COMMENT '引擎模式',
                                    `flow_id` int(11) NULL DEFAULT NULL COMMENT '流程关联ID',
                                    `flow_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程名称',
                                    `core_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '核心配置JSON',
                                    `job_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '作业配置JSON',
                                    `custom_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自定义配置JSON',
                                    `schedule_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '调度任务配置JSON',
                                    `remark_msg` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                    `user_id` int(11) NULL DEFAULT NULL COMMENT '创建用户ID',
                                    `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户名称',
                                    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `dsp_application_job_id_IDX`(`job_id`, `app_name`) USING BTREE,
                                    INDEX `dsp_application_flow_id_IDX`(`flow_id`, `flow_name`) USING BTREE,
                                    INDEX `dsp_application_deploy_id_IDX`(`deploy_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Dps应用表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dsp_application
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_application_deploy
-- ----------------------------
DROP TABLE IF EXISTS `dsp_application_deploy`;
CREATE TABLE `dsp_application_deploy`  (
                                           `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
                                           `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联的应用ID',
                                           `job_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作业ID',
                                           `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作业名称',
                                           `job_state` int(11) NULL DEFAULT NULL COMMENT '作业状态(Application状态)',
                                           `web_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作业监控地址',
                                           `engine_type` int(11) NULL DEFAULT NULL COMMENT '引擎类型',
                                           `engine_mode` int(11) NULL DEFAULT NULL COMMENT '引擎模式',
                                           `job_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '作业JSON',
                                           `job_json_md5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作业JSON的MD5',
                                           `job_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作业文件',
                                           `restart_mark` int(11) NULL DEFAULT NULL COMMENT '重启标记',
                                           `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
                                           `end_time` timestamp NULL DEFAULT NULL COMMENT '停止时间',
                                           `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `dsp_application_deploy_job_id_IDX`(`job_id`, `job_name`) USING BTREE,
                                           INDEX `dsp_application_deploy_job_state_IDX`(`job_state`) USING BTREE,
                                           INDEX `dsp_application_deploy_app_id_IDX`(`app_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 180 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Dps应用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dsp_application_deploy
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_application_flow
-- ----------------------------
DROP TABLE IF EXISTS `dsp_application_flow`;
CREATE TABLE `dsp_application_flow`  (
                                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
                                         `app_id` int(11) NOT NULL COMMENT '应用id',
                                         `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
                                         `job_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '作业id',
                                         `flow_pk` int(11) NOT NULL COMMENT '流程主键id',
                                         `flow_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程id',
                                         `flow_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程名称',
                                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 136 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '应用流程关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dsp_application_flow
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_config
-- ----------------------------
DROP TABLE IF EXISTS `dsp_config`;
CREATE TABLE `dsp_config`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                               `config_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置id',
                               `config_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置名称',
                               `config_category` int(11) NULL DEFAULT NULL COMMENT '源类型',
                               `config_type` int(11) NULL DEFAULT NULL COMMENT '配置类型',
                               `config_map` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置内容',
                               `disable_mark` int(11) NULL DEFAULT NULL COMMENT '禁用标识',
                               `remark_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                               `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
                               `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dsp_config
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_flow
-- ----------------------------
DROP TABLE IF EXISTS `dsp_flow`;
CREATE TABLE `dsp_flow`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
                             `flow_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程ID',
                             `flow_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程名称',
                             `flow_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '流程后端JSON',
                             `flow_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程前端JSON',
                             `disable_mark` int(11) NULL DEFAULT NULL COMMENT '禁用标识',
                             `update_mark` int(11) NULL DEFAULT NULL COMMENT '更新标记',
                             `remark_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
                             `update_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改用户',
                             `create_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `dsp_flow_flow_id_IDX`(`flow_id`, `flow_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 112 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Dps应用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dsp_flow
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_flow_config
-- ----------------------------
DROP TABLE IF EXISTS `dsp_flow_config`;
CREATE TABLE `dsp_flow_config`  (
                                    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                    `config_pk` int(11) NULL DEFAULT NULL COMMENT '配置pk',
                                    `config_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置id',
                                    `config_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置名称',
                                    `flow_pk` int(11) NULL DEFAULT NULL COMMENT '插件pk',
                                    `flow_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件id',
                                    `flow_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件名称',
                                    `node_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点id',
                                    `plugin_index` int(11) NULL DEFAULT NULL COMMENT '插件索引',
                                    `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点配置项key',
                                    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dsp_flow_config
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_flow_plugin
-- ----------------------------
DROP TABLE IF EXISTS `dsp_flow_plugin`;
CREATE TABLE `dsp_flow_plugin`  (
                                    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                    `flow_pk` int(11) NULL DEFAULT NULL COMMENT '流主键id',
                                    `flow_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流id',
                                    `flow_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流名称',
                                    `plugin_jar_pk` int(11) NULL DEFAULT NULL COMMENT 'plugin_jar主键id',
                                    `plugin_jar_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'plugin_jar id',
                                    `plugin_jar_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'plugin_jar名称',
                                    `plugin_pk` int(11) NULL DEFAULT NULL COMMENT '插件主键id',
                                    `plugin_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件id',
                                    `plugin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件名称',
                                    `plugin_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件class',
                                    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 361 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dsp_flow_plugin
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_plugin
-- ----------------------------
DROP TABLE IF EXISTS `dsp_plugin`;
CREATE TABLE `dsp_plugin`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
                               `plugin_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件id(class的md5)',
                               `plugin_jar_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属jar包md5',
                               `plugin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '插件名称',
                               `plugin_type` int(11) NULL DEFAULT NULL COMMENT '插件类型',
                               `plugin_alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件别名',
                               `plugin_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '插件class',
                               `plugin_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件备注',
                               `plugin_configs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '插件配置模板',
                               `plugin_infos` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件ui信息项',
                               `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 646 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dsp_plugin
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_plugin_jar
-- ----------------------------
DROP TABLE IF EXISTS `dsp_plugin_jar`;
CREATE TABLE `dsp_plugin_jar`  (
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                   `plugin_jar_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'jar包id',
                                   `plugin_jar_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'jar包名称',
                                   `plugin_jar_alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'jar包别名',
                                   `plugin_jar_md5` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'jar包md5',
                                   `plugin_jar_origin` int(11) NULL DEFAULT NULL COMMENT 'jar包来源',
                                   `plugin_jar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'jar包url',
                                   `plugin_def_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'jar包定义文件名',
                                   `plugin_def_file_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'jar包定义文件内容,json格式,用于解析',
                                   `disable_mark` int(11) NULL DEFAULT NULL COMMENT '禁用标识',
                                   `remark_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                   `file_size` bigint(64) NULL DEFAULT NULL COMMENT '文件大小',
                                   `upload_time` timestamp NULL DEFAULT NULL COMMENT '上传时间',
                                   `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
                                   `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 161 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dsp_plugin_jar
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_schedule_task
-- ----------------------------
DROP TABLE IF EXISTS `dsp_schedule_task`;
CREATE TABLE `dsp_schedule_task`  (
                                      `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务id',
                                      `task_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务UUID',
                                      `task_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务名称',
                                      `task_type` int(11) NULL DEFAULT NULL COMMENT '任务类型',
                                      `task_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务类',
                                      `task_configs` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '任务配置',
                                      `task_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务备注',
                                      `task_cron_expr` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'cron表达式',
                                      `task_status` int(11) NOT NULL DEFAULT 0 COMMENT '状态 1启动 0 停止',
                                      `create_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人name 关联sys_user',
                                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '定时任务' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dsp_schedule_task
-- ----------------------------

-- ----------------------------
-- Table structure for dsp_unresolved_log
-- ----------------------------
DROP TABLE IF EXISTS `dsp_unresolved_log`;
CREATE TABLE `dsp_unresolved_log`  (
                                       `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                       `job_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作业id',
                                       `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作业名称',
                                       `node_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点id',
                                       `node_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点名称',
                                       `node_type` int(11) NULL DEFAULT NULL COMMENT '节点类型',
                                       `unresolved_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '未解析来源',
                                       `unresolved_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '未解析类型',
                                       `unresolved_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                       `unresolved_date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '未解析日期',
                                       `unresolved_time` timestamp NULL DEFAULT NULL COMMENT '未解析时间',
                                       `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dsp_unresolved_log
-- ----------------------------

-- ----------------------------
-- Table structure for flink_dsp_version
-- ----------------------------
DROP TABLE IF EXISTS `flink_dsp_version`;
CREATE TABLE `flink_dsp_version`  (
                                      `installed_rank` int(11) NOT NULL,
                                      `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                      `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                      `script` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                      `checksum` int(11) NULL DEFAULT NULL,
                                      `installed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                      `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `execution_time` int(11) NOT NULL,
                                      `success` tinyint(1) NOT NULL,
                                      PRIMARY KEY (`installed_rank`) USING BTREE,
                                      INDEX `flink_dsp_version_s_idx`(`success`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flink_dsp_version
-- ----------------------------
INSERT INTO `flink_dsp_version` VALUES (1, '1.0.0', 'dsp console', 'SQL', 'V1.0.0__dsp_console.sql', -1628861795, 'weiwan', '2021-10-31 01:27:20', 929, 1);

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
                                   `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
                                   `type` tinyint(1) NOT NULL COMMENT '资源类型(1.菜单 2.按钮或文本块)',
                                   `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父编号',
                                   `parent_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父编号列表',
                                   `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限字符串',
                                   `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
                                   `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
                                   `status` tinyint(1) NULL DEFAULT 1 COMMENT '是否有效',
                                   `config` json NULL COMMENT '权限配置',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `idx_sys_permission_parent_id`(`parent_id`) USING BTREE,
                                   INDEX `idx_sys_permission_parent_ids`(`parent_ids`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 197 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, '状态概览', 1, 0, '0/', 'overview', 'dashboard', 0, 1, '{\"meta\": {\"icon\": \"dashboard\", \"show\": true, \"title\": \"route.overview\"}, \"name\": \"overview\", \"redirect\": \"/overview/dashboard\", \"component\": \"RouteView\"}');
INSERT INTO `sys_permission` VALUES (2, '仪表盘', 1, 1, '0/1/', 'dashboard', 'laptop', 0, 1, '{\"meta\": {\"icon\": \"laptop\", \"show\": true, \"title\": \"route.dashboard\"}, \"name\": \"dashboard\", \"component\": \"Dashboard\"}');
INSERT INTO `sys_permission` VALUES (3, '运行状态', 1, 1, '0/1/', 'running', 'fund', 0, 1, '{\"meta\": {\"icon\": \"fund\", \"show\": true, \"title\": \"route.running\"}, \"name\": \"running\", \"component\": \"Running\"}');
INSERT INTO `sys_permission` VALUES (10, '系统管理', 1, 0, '0/10/', 'user:mgr', 'user', 3, 1, '{\"meta\": {\"icon\": \"user\", \"show\": true, \"title\": \"route.system\"}, \"name\": \"user\", \"redirect\": \"/user/system-user\", \"component\": \"PageView\"}');
INSERT INTO `sys_permission` VALUES (11, '用户列表', 1, 10, '0/10/', 'user:view', 'user', 0, 1, '{\"meta\": {\"icon\": \"user\", \"show\": true, \"title\": \"route.users\"}, \"name\": \"user-list\", \"component\": \"UserList\"}');
INSERT INTO `sys_permission` VALUES (12, '角色列表', 1, 10, '0/10/', 'role:view', 'team', 0, 1, '{\"meta\": {\"icon\": \"team\", \"show\": true, \"title\": \"route.roles\"}, \"name\": \"role-list\", \"component\": \"RoleList\"}');
INSERT INTO `sys_permission` VALUES (13, '权限列表', 1, 10, '0/10/', 'permission:view', 'tool', 0, 1, '{\"meta\": {\"icon\": \"tool\", \"show\": true, \"title\": \"route.permission\"}, \"name\": \"permission-list\", \"component\": \"PermissionList\"}');
INSERT INTO `sys_permission` VALUES (21, '仪表盘查看', 2, 2, '0/1/3/', 'dashboard:view', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (31, '运行状态查看', 2, 3, '0/1/4', 'running:view', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (111, '用户查看', 2, 11, '0/10/11/', 'user:view', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (112, '用户新增', 2, 11, '0/10/11/', 'user:create', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (113, '用户修改', 2, 11, '0/10/11/', 'user:update', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (114, '用户删除', 2, 11, '0/10/11/', 'user:delete', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (121, '角色查看', 2, 12, '0/10/12/', 'role:view', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (122, '角色新增', 2, 12, '0/10/12/', 'role:create', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (123, '角色修改', 2, 12, '0/10/12/', 'role:update', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (124, '角色删除', 2, 12, '0/10/12/', 'role:delete', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (131, '权限查看', 2, 13, '0/10/13/', 'permission:view', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (132, '权限新增', 2, 13, '0/10/13/', 'permission:create', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (133, '权限修改', 2, 13, '0/10/13/', 'permission:update', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (134, '权限删除', 2, 13, '0/10/13/', 'permission:delete', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (141, '数据处理', 1, 0, '0/', 'dsp', 'heat-map', 0, 1, '{\"meta\": {\"icon\": \"heat-map\", \"show\": true, \"title\": \"route.process\"}, \"name\": \"dsp\", \"redirect\": \"/dsp/application\", \"component\": \"RouteView\"}');
INSERT INTO `sys_permission` VALUES (143, '流程管理', 1, 141, '0/141/', 'flow', 'share-alt', 2, 1, '{\"meta\": {\"icon\": \"share-alt\", \"show\": true, \"title\": \"route.flow\"}, \"name\": \"flow\", \"component\": \"Flow\"}');
INSERT INTO `sys_permission` VALUES (152, '流程管理查看', 2, 143, '0/141/143/', 'flow:view', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (153, '应用管理', 1, 141, '0/141/', 'application', 'profile', 1, 1, '{\"meta\": {\"icon\": \"profile\", \"show\": true, \"title\": \"route.application\", \"hideHeader\": true, \"hideChildren\": true}, \"name\": \"application\", \"component\": \"Application\"}');
INSERT INTO `sys_permission` VALUES (154, '应用管理查看', 2, 153, '0/141/153/', 'application:view', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (155, '插件管理', 1, 141, '0/141/', 'plugin', 'api', 3, 1, '{\"meta\": {\"icon\": \"api\", \"show\": true, \"title\": \"route.plugin\"}, \"name\": \"plugin\", \"component\": \"Plugin\"}');
INSERT INTO `sys_permission` VALUES (156, '插件管理查看', 2, 155, '0/141/155/', 'plugin:view', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (162, '应用管理新增', 2, 153, '0/141/153/', 'application:create', NULL, 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (164, '新建应用页面', 1, 141, '0/141/', 'application:create', 'question', 0, 1, '{\"meta\": {\"icon\": \"question\", \"show\": false, \"title\": \"route.applicationDetail\", \"keepAlive\": true}, \"name\": \"ApplicationCreate\", \"path\": \"/dsp/application/create\", \"component\": \"ApplicationCreate\"}');
INSERT INTO `sys_permission` VALUES (165, '新增流程按钮', 2, 143, '0/141/143/', 'flow:create', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (166, '新建流程页面', 1, 141, '0/141/', 'flow:create', 'question', 0, 1, '{\"meta\": {\"icon\": \"question\", \"show\": false, \"title\": \"route.flowDetail\", \"keepAlive\": true}, \"name\": \"FlowCreate\", \"path\": \"/dsp/flow/create\", \"component\": \"FlowCreate\"}');
INSERT INTO `sys_permission` VALUES (167, '流程按钮(禁用/启用)', 2, 143, '0/141/143/', 'flow:disable', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (168, '修改流程按钮', 2, 143, '0/141/143/', 'flow:update', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (169, '删除流程按钮', 2, 143, '0/141/143/', 'flow:delete', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (173, '应用管理修改', 2, 153, '0/141/153/', 'application:update', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (174, '应用管理删除', 2, 153, '0/141/153/', 'application:delete', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (175, '应用管理启动', 2, 153, '0/141/153/', 'application:start', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (179, '插件禁用按钮', 2, 155, '0/141/155/', 'plugin:disable', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (180, '插件删除按钮', 2, 155, '0/141/155/', 'plugin:delete', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (181, '插件上传按钮', 2, 155, '0/141/155/', 'plugin:upload', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (183, '插件更新按钮', 2, 155, '0/141/155/', 'plugin:update', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (184, '插件创建按钮', 2, 155, '0/141/155/', 'plugin:create', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (185, '运维调度', 1, 0, '0/', 'devops', 'profile', 2, 1, '{\"meta\": {\"icon\": \"profile\", \"show\": true, \"title\": \"route.devops\"}, \"name\": \"devops\", \"redirect\": \"/devops/schedule\", \"component\": \"RouteView\"}');
INSERT INTO `sys_permission` VALUES (186, '任务调度', 1, 185, '0/185/', 'schedule', 'fund', 0, 1, '{\"meta\": {\"icon\": \"fund\", \"show\": true, \"title\": \"route.schedule\"}, \"name\": \"schedule\", \"component\": \"Schedule\"}');
INSERT INTO `sys_permission` VALUES (187, '任务调度查看', 2, 186, '0/185/186/', 'schedule:view', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (188, '配置管理', 1, 185, '0/185/', 'config', 'file-done', 1, 1, '{\"meta\": {\"icon\": \"file-done\", \"show\": true, \"title\": \"route.config\"}, \"name\": \"config\", \"component\": \"Config\"}');
INSERT INTO `sys_permission` VALUES (189, '配置查看', 2, 188, '0/185/188/', 'config:view', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (190, '新建配置', 2, 188, '0/185/188/', 'config:create', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (191, '修改配置', 2, 188, '0/185/188/', 'config:update', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (192, '删除配置', 2, 188, '0/185/188/', 'config:delete', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (194, '流程下载按钮', 2, 143, '0/141/143/', 'flow:download', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (195, '流程上传按钮', 2, 143, '0/141/143/', 'flow:upload', 'question', 0, 1, NULL);
INSERT INTO `sys_permission` VALUES (196, '应用管理停止', 2, 153, '0/141/153/', 'application:stop', 'question', 0, 1, NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
                             `role` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一标识',
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
                             `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
                             `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态 1.正常 0.禁用',
                             `permission_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源编号列表',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `idx_sys_role_resource_ids`(`permission_ids`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'admin', '管理员', '拥有所有权限', 1, '1,21,31,141,154,162,173,174,175,196,152,165,167,168,169,194,195,156,179,180,181,183,184,185,187,189,190,191,192,10,111,112,113,114,121,122,123,124,131,132,133,134');
INSERT INTO `sys_role` VALUES (2, 'guest', '访客', '访问人员，只能查询', 1, '1,21,31,141,154,162,173,174,175,152,165,167,168,169,156,10,111,112,113,114,121,122,123,124,131,132,133,134');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
                             `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
                             `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                             `salt` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐值',
                             `role_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色列表',
                             `locked` tinyint(1) NULL DEFAULT 0 COMMENT '是否锁定',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `idx_sys_user_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '4473a6d0b22ab625fc466d9b96ffe1ff', '39aadef8b0a74aed47cd2b582f7865cf', '1', 0);
INSERT INTO `sys_user` VALUES (2, 'guest', '5de6e625cd37ab55916edb92b2959306', 'a7694be8a2db593167b3b6f6693e4727', '1', 0);

SET FOREIGN_KEY_CHECKS = 1;
