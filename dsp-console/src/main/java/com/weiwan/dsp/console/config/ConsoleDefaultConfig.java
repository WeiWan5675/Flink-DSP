package com.weiwan.dsp.console.config;

import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.common.constants.Constants;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/28 15:20
 * @description:
 */
@Configuration
public class ConsoleDefaultConfig {


    @Bean(name = "consoleDefaultProperties")
    @PostConstruct
    public Properties getProperties() {
        YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
        yamlMapFactoryBean.setResources(new ClassPathResource("conf" + File.separator + DspConstants.DSP_CONSOLE_DEFAULT_CONFIG_FILE));
        Properties properties = yamlMapFactoryBean.getObject();
        return properties;
    }


    @Bean(name = "consoleDBProperties")
    @PostConstruct
    public Properties getDBProperties() throws MalformedURLException {
        YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
        String confDir = System.getProperty(DspConstants.DSP_CONF_DIR);
        yamlMapFactoryBean.setResources(new FileUrlResource(confDir + File.separator + DspConstants.DSP_CONSOLE_DB_CONFIG_FILE));
        Properties properties = yamlMapFactoryBean.getObject();
        return properties;
    }

}
