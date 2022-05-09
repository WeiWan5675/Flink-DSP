package com.weiwan.dsp.console;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${dsp.console.report.port}")
    private String reportPort;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void myObjectMapper() {
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);
        objectMapper.configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true);
    }

    @Bean
    public TomcatServletWebServerFactory loadReporterWebContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        Connector[] connectors = this.getWebConnector();
        if (connectors != null && connectors.length > 0) {
            tomcat.addAdditionalTomcatConnectors(connectors);
        }
        return tomcat;
    }

    private Connector[] getWebConnector() {
        Connector reportConnector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        reportConnector.setScheme("http");
        //添加一个额外的端口监听,用来进行未解析数据和监控数据的上报
        reportConnector.setPort(Integer.valueOf(reportPort));
        return new Connector[]{reportConnector};
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/dsp/docs/").addResourceLocations("classpath:/META-INF/resources/webjars/docs/");
        registry.addResourceHandler("/dsp/docs/**").addResourceLocations("classpath:/META-INF/resources/webjars/docs/");
        registry.addResourceHandler("/swagger-ui/").addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
        registry.addResourceHandler("/swagger-resources/").addResourceLocations("classpath:/META-INF/resources/webjars/swagger-resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/webjars/dsp-console-web/");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        SimpleModule simpleModule = new SimpleModule();
        objectMapper.registerModule(simpleModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(0, jackson2HttpMessageConverter);
    }

    @Bean
    public WebServerFactoryCustomizer containerCustomizer() {
        return new WebServerFactoryCustomizer() {
            @Override
            public void customize(WebServerFactory factory) {
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
                ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/index.html");
                ((TomcatServletWebServerFactory) factory).addErrorPages(error404Page, error401Page);
            }

        };
    }

    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        ErrorPageRegistrar errorPageRegistrar = new ErrorPageRegistrar() {
            @Override
            public void registerErrorPages(ErrorPageRegistry registry) {
                ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
                ErrorPage errorPage401 = new ErrorPage(HttpStatus.UNAUTHORIZED, "/index.html");
                registry.addErrorPages(errorPage404, errorPage401);
            }
        };
        return errorPageRegistrar;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/dsp/docs").setViewName("docs/index.html");
    }


    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}

