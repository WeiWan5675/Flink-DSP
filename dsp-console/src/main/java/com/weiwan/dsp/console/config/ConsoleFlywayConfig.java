package com.weiwan.dsp.console.config;

import com.weiwan.dsp.common.enums.DbType;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.util.Locale;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/23 19:13
 * @description:
 */
@Configuration
public class ConsoleFlywayConfig {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleDataSourceConfig.class);

    @Autowired
    @Qualifier("dspDynamicDataSource") //配置中定义的名字
    private DataSource dspDynamicDataSource;

    @Value("${dsp.console.database.upgrade}")
    private boolean enableAutoUpdate;

    @Autowired
    private ConsoleDataSourceConfig consoleDataSourceConfig;

    @Autowired
    private ConsoleDBIniter consoleDBIniter;

    @PostConstruct
    @DependsOn({"consoleDBConfig", "ConsoleDBIniter"})
    public void migrate() {
        if (enableAutoUpdate) {
            logger.info("Check whether the current database version needs to be upgraded.");
            DbType dbType = consoleDataSourceConfig.getDBType();
            //默认升级脚本,实际升级脚本在classoath:db/xxxDB
            String flywayLocations = "db" + File.separator + "migration";
            switch (dbType) {
                case MYSQL:
                    flywayLocations = "classpath:db" + File.separator + DbType.MYSQL.name().toLowerCase(Locale.ROOT);
                    break;
                case ORACLE:
                case POSTGRESQL:
                case SQLSERVER:
                    throw new RuntimeException("Unsupported database");
                default:
                    throw new RuntimeException("Unknown database type");
            }
            Flyway flyway = Flyway.configure()
                    .schemas(consoleDataSourceConfig.getDbName())
                    .table("flink_dsp_version")
                    .encoding("utf-8")
                    .dataSource(dspDynamicDataSource)
                    .locations(new Location(flywayLocations))
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();
            logger.info("Database version verification or upgrade has been completed");
        } else {
            logger.info("The database upgrade is closed, and the database needs to be initialized (please ignore when finished))");
        }

    }
}
