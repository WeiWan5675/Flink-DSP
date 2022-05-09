package com.weiwan.dsp.console.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2021/8/21 15:36
 * @Package: com.weiwan.dsp.console.config
 * @ClassName: ConsoleMyBatisPlusConfig
 * @Description: MyBatisPlusConfig
 **/

@Configuration
@EnableConfigurationProperties(MybatisPlusProperties.class)
@EnableTransactionManagement
@Slf4j
public class ConsoleMyBatisPlusConfig {

    @Autowired
    private MybatisPlusProperties properties;

    @Autowired(required = false)
    private Interceptor[] interceptors;

    @Autowired(required = false)
    @Qualifier("vendorDatabaseIdProvider")
    private DatabaseIdProvider databaseIdProvider;

    @Autowired
    private ConsoleDataSourceConfig.ConsoleDBConfig consoleDBConfig;

    @Bean
    @ConditionalOnBean(MybatisSqlSessionFactoryBean.class) // 当 SqlSessionFactoryBean 实例存在时创建对象
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.weiwan.dsp.console.mapper");
        return mapperScannerConfigurer;
    }


    @Bean("sqlSessionFactory")
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(MybatisConfiguration mybatisConfiguration, GlobalConfig globalConfig, DataSource dspDynamicDataSource) throws IOException {
        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
        mybatisPlus.setDataSource(dspDynamicDataSource);
        mybatisPlus.setVfs(SpringBootVFS.class);

        //mapper文件的路径,按照不同数据库做路径区分  db/${dbType}/mapper/*.xml
        String dbType = consoleDBConfig.getDbType().name().toLowerCase();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = new Resource[0];
        try {
            resources = resolver.getResources("classpath:db/" + dbType + "/mapper/*.xml");
        } catch (IOException e) {
            log.warn("Can not found mapper xml resource");
        }
        if(resources.length != 0){
            mybatisPlus.setMapperLocations(resources);
        }

        mybatisPlus.setConfiguration(mybatisConfiguration);
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            mybatisPlus.setPlugins(this.interceptors);
        }


        //设置全局globalConfig
        mybatisPlus.setGlobalConfig(globalConfig);

        //设置idProvider
        mybatisPlus.setDatabaseIdProvider(databaseIdProvider);

        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            mybatisPlus.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            mybatisPlus.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            mybatisPlus.setMapperLocations(this.properties.resolveMapperLocations());
        }
        return mybatisPlus;
    }

    @Bean
    public VendorDatabaseIdProvider vendorDatabaseIdProvider() {
        VendorDatabaseIdProvider vendorDatabaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("MySql", "mysql");
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("SqlServer", "sqlserver");
        vendorDatabaseIdProvider.setProperties(properties);
        return vendorDatabaseIdProvider;

    }


    @Bean(name = "mybatisConfiguration")
    public MybatisConfiguration configuration() {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        mybatisConfiguration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        return mybatisConfiguration;
    }


    @Bean
    public GlobalConfig globalConfig(GlobalConfig.DbConfig dbConfig) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDbConfig(dbConfig);
        globalConfig.setBanner(true);
        globalConfig.setSqlInjector(new DefaultSqlInjector());
        globalConfig.setEnableSqlRunner(true);
        return globalConfig;
    }


    @Bean
    public GlobalConfig.DbConfig dbConfig() {
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIdType(IdType.AUTO);
        dbConfig.setTableUnderline(true);
        dbConfig.setCapitalMode(false);
        return dbConfig;
    }


    @Bean
    public Interceptor[] interceptors(ConsoleDataSourceConfig.ConsoleDBConfig consoleDBConfig) {
        List<Interceptor> interceptors = new ArrayList<>();
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        Properties properties = new Properties();
        properties.setProperty("@page", "com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor");
        properties.setProperty("page:dbType", consoleDBConfig.getDbType().name());
        mybatisPlusInterceptor.setProperties(properties);
        interceptors.add(mybatisPlusInterceptor);
        return interceptors.toArray(new Interceptor[interceptors.size()]);
    }


}
