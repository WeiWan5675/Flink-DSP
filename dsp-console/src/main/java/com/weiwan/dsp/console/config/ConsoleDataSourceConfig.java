package com.weiwan.dsp.console.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.weiwan.dsp.common.enums.DbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.CompositeDatabasePopulator;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

@Configuration
public class ConsoleDataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleDataSourceConfig.class);
    @Value("${dsp.datasource.druid.initialSize}")
    private int initialSize;

    @Value("${dsp.datasource.druid.minIdle}")
    private int minIdle;

    @Value("${dsp.datasource.druid.maxActive}")
    private int maxActive;

    @Value("${dsp.datasource.druid.maxWait}")
    private int maxWait;

    @Value("${dsp.datasource.druid.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${dsp.datasource.druid.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${dsp.datasource.druid.maxEvictableIdleTimeMillis}")
    private int maxEvictableIdleTimeMillis;

    @Value("${dsp.datasource.druid.validationQuery}")
    private String validationQuery;

    @Value("${dsp.datasource.druid.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${dsp.datasource.druid.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${dsp.datasource.druid.testOnReturn}")
    private boolean testOnReturn;

    @Value("${dsp.datasource.dbType:mysql}")
    private String dbType;
    @Value("${dsp.datasource.showSql:false}")
    private boolean showSql;

    @Value("${dsp.datasource.formatSql:true}")
    private boolean formatSql;

    @Value("${dsp.datasource.dbName:flink_dsp}")
    private String dbName;

    @Autowired
    private Properties consoleDBProperties;


    public DruidDataSource druidDataSource(DruidDataSource dataSource) {
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
//        try {
//            dataSource.addFilters("stat,wall");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return dataSource;
    }

    @Bean
    public WallFilter wallFilter(){
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

    @Bean
    public WallConfig wallConfig(){
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);//允许一次执行多条语句
        wallConfig.setNoneBaseStatementAllow(true);//允许一次执行多条语句
        return wallConfig;
    }

    /**
     * 默认的H2内存数据库，在没有安装系统之前使用该数据库
     *
     * @param consoleDataSourceConfig druid配置属性
     * @return DruidDataSource
     */
//    @Bean(name = "dspH2DataSource")
    @ConfigurationProperties(prefix = "dsp.datasource.h2")
    public DataSource h2DataSource(ConsoleDataSourceConfig consoleDataSourceConfig) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return consoleDataSourceConfig.druidDataSource(dataSource);
    }

    /**
     * Oracle数据源
     *
     * @param consoleDataSourceConfig druid配置属性
     * @return DruidDataSource
     */
//    @Bean(name = "dspOracleDataSource")
    @ConfigurationProperties(prefix = "dsp.datasource.oracle")
    public DataSource oracleDataSource(ConsoleDataSourceConfig consoleDataSourceConfig) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return consoleDataSourceConfig.druidDataSource(dataSource);
    }

    /**
     * sqlServer数据源
     *
     * @param consoleDataSourceConfig druid配置属性
     * @return DruidDataSource
     */
//    @Bean(name = "dspSqlServerDataSource")
    @ConfigurationProperties(prefix = "dsp.datasource.sqlserver")
    public DataSource sqlServerDataSource(ConsoleDataSourceConfig consoleDataSourceConfig) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return consoleDataSourceConfig.druidDataSource(dataSource);
    }


    /**
     * sqlServer数据源
     *
     * @param consoleDataSourceConfig druid配置属性
     * @return DruidDataSource
     */
//    @Bean(name = "dspPostgreSqlDataSource")
    @ConfigurationProperties(prefix = "dsp.datasource.postgresql")
    public DataSource postgreSqlDataSource(ConsoleDataSourceConfig consoleDataSourceConfig) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return consoleDataSourceConfig.druidDataSource(dataSource);
    }

    /**
     * 配置数据库后使用该数据源
     *
     * @param consoleDataSourceConfig druid配置属性
     * @return DruidDataSource
     */
    @Bean(name = "dspMysqlDataSource")
    @ConfigurationProperties(prefix = "dsp.datasource.mysql")
    public DataSource mysqlDataSource(ConsoleDataSourceConfig consoleDataSourceConfig) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return consoleDataSourceConfig.druidDataSource(dataSource);
    }

    public DbType getDBType() {
        return DbType.valueOf(dbType.toUpperCase(Locale.ROOT));
    }


    @Bean
    public PlatformTransactionManager pgTransactionManager(@Qualifier("dspDynamicDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "dspDynamicDataSource")
    @SuppressWarnings("all")
    public DataSource dynamicDataSource(DataSource dspMysqlDataSource, DataSource dspOracleDataSource, DataSource dspSqlServerDataSource, DataSource dspPostgreSqlDataSource) {
        switch (DbType.valueOf(dbType.toUpperCase(Locale.ROOT))) {
            case MYSQL:
                logger.debug("Use MySQL data source.");
                return dspMysqlDataSource;
            case ORACLE:
                logger.debug("Use Oracle data source.");
                return dspOracleDataSource;
            case SQLSERVER:
                logger.debug("Use SqlServer data source.");
                return dspSqlServerDataSource;
            case POSTGRESQL:
                logger.debug("Use PostgreSql data source.");
                return dspPostgreSqlDataSource;
            default:
                logger.error("unknown database type please check configuration");
                throw new RuntimeException("unknown database type please check configuration");
        }
    }


    @Bean(name = "consoleDBConfig")
    public ConsoleDBConfig dbConfig() {
        String jdbcUrl = consoleDBProperties.getProperty("dsp.datasource." + dbType + ".url");
        String username = consoleDBProperties.getProperty("dsp.datasource." + dbType + ".username");
        String password = consoleDBProperties.getProperty("dsp.datasource." + dbType + ".password");
        String dbSchema = consoleDBProperties.getProperty("dsp.datasource." + dbType + ".name");
        String driverClassName = consoleDBProperties.getProperty("dsp.datasource." + dbType + ".driver-class-name");
        jdbcUrl = jdbcUrl.replaceAll("/" + dbName, "");
        ConsoleDBConfig consoleDBConfig = new ConsoleDBConfig();
        consoleDBConfig.setUsername(username);
        consoleDBConfig.setPassword(password);
        consoleDBConfig.setJdbcUrl(jdbcUrl);
        consoleDBConfig.setDriveClass(driverClassName);
        consoleDBConfig.setDbType(getDBType());
        consoleDBConfig.setDbSchema(dbSchema);
        return consoleDBConfig;
    }


    public boolean isShowSql() {
        return showSql;
    }

    public boolean isFormatSql() {
        return formatSql;
    }


    public String getDbType() {
        return dbType;
    }

    public String getDbName() {
        return dbName;
    }


    class ConsoleDBConfig implements Serializable {

        private static final long serialVersionUID = -2140662574535998005L;
        private String username;
        private String password;
        private String jdbcUrl;
        private String dbSchema;
        private String driveClass;
        private DbType dbType;


        public DbType getDbType() {
            return dbType;
        }

        public void setDbType(DbType dbType) {
            this.dbType = dbType;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getDbSchema() {
            return dbSchema;
        }

        public void setDbSchema(String dbSchema) {
            this.dbSchema = dbSchema;
        }

        public String getDriveClass() {
            return driveClass;
        }

        public void setDriveClass(String driveClass) {
            this.driveClass = driveClass;
        }

        public Connection getConnection() throws Exception {
//注册数据库驱动
            Class.forName(this.getDriveClass());
            //取得数据库连接
            Connection connection = DriverManager.getConnection(this.getJdbcUrl(), this.getUsername(), this.getPassword());
            return connection;
        }
    }
}