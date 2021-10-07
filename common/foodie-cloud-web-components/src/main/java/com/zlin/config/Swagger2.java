package com.zlin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author zlin
 * @date 20201221
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    /**
     * http://localhost:8088/swagger-ui.html  springfox-swagger-ui原路径
     * http://localhost:8088/doc.html         swagger-bootstrap-ui原路径
     * 配置swagger2核心配置Docket
     * @return Docket
     */
    @Bean
    public Docket createRestApi() {
        // 指定api类型为swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                // 用于定义api文档汇总信息
                .apiInfo(apiInfo())
                // 指定controller包
                .select().apis(
                        RequestHandlerSelectors
                        // .basePackage("com.zzlin.controller")
                        .withClassAnnotation(RestController.class)
                )
                // 所有controller
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 文档页标题
                .title("天天吃货 电商平台接口api")
                // 联系人信息
                .contact(new Contact("zzlin",
                        "https://www.imooc.com",
                        "abc@imooc.com"))
                // 详细信息
                .description("专为天天吃货提供的api文档")
                // 文档版本号
                .version("1.0.1")
                // 网站地址
                .termsOfServiceUrl("https://www.baidu.com")
                .build();
    }
}
