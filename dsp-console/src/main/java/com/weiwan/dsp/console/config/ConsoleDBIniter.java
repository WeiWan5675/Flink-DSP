package com.weiwan.dsp.console.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/27 15:20
 * @description:
 */
@Configurable
@Component
@Order
public class ConsoleDBIniter {

    private static final Map<String, String> initSqlMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ConsoleDBIniter.class);

    {
        initSqlMap.put("mysql", "CREATE DATABASE IF NOT EXISTS ${dbName} DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;");
    }

    @Autowired
    @Qualifier("consoleDBConfig")
    private ConsoleDataSourceConfig.ConsoleDBConfig consoleDBConfig;

    @Value("${dsp.console.database.create}")
    private boolean enableAutoCreate;

    @PostConstruct
    public void initConsoleDB() throws Exception {
        if (!enableAutoCreate) {
            logger.info("Skip auto create the metadata database, please create it (please ignore when finished)");
            return;
        };
        Connection connection = null;
        Statement statement = null;
        try {
            String initSql = initSqlMap.get(consoleDBConfig.getDbType().name().toLowerCase(Locale.ROOT));
            initSql = initSql.replace("${dbName}", consoleDBConfig.getDbSchema());
            String jdbcUrl = consoleDBConfig.getJdbcUrl();
            String newJdbcUrl = jdbcUrl.replace("/" + consoleDBConfig.getDbSchema(), "");
            Class.forName(consoleDBConfig.getDriveClass());
            //取得数据库连接
            connection = DriverManager.getConnection(newJdbcUrl, consoleDBConfig.getUsername(), consoleDBConfig.getPassword());
            statement = connection.createStatement();
            boolean execute = statement.execute(initSql);
            logger.info("Database initialization succeeded.");
        } catch (Exception e) {
            if(logger.isDebugEnabled()){
                logger.warn("Failed to create database, the database may already exist", e);
            }
        } finally {
            try {
                connection.close();
                statement.close();
            } catch (Exception throwables) {
            }
        }

    }

}
