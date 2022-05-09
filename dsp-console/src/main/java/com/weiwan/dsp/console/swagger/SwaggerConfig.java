package com.weiwan.dsp.console.swagger;

import com.weiwan.dsp.api.constants.DspConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spi.service.contexts.SecurityContextBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Configuration
public class SwaggerConfig {


    @Autowired
    @Qualifier("consoleDefaultProperties")
    private Properties consoleDefaultProperties;


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.weiwan.dsp.console.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Flink-DSP接口文档")
                .description("Flink-DSP | Flink数据处理平台")
                .termsOfServiceUrl("https://github.com/WeiWan5675/Flink-DSP")
                .version((String) consoleDefaultProperties.get(DspConstants.DSP_VERSION))
                .build();
    }

    private List<SecurityScheme> securitySchemes() {
        //参数1：字段的名字，参数2：字段的键，参数3：参数位置
        return Arrays.asList(new ApiKey("Access-Token", "Access-Token", "header"));
    }

    //认证的上下文，这里面需要指定哪些接口需要认证
    private List<SecurityContext> securityContexts() {
        SecurityContextBuilder builder = SecurityContext.builder().securityReferences(securityReferences());
        //指定需要认证的path，大写的注意，这里就用到了配置文件里面的URL，需要自己实现path选择的逻辑
//        builder.forPaths(null);
        return Arrays.asList(builder.build());
    }

    //这个方法是验证的作用域，不能漏了
    private List<SecurityReference> securityReferences() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        return Arrays.asList(
                new SecurityReference("Access-Token", new AuthorizationScope[]{authorizationScope}));
    }

}
