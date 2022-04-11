package com.bkexercise.jobapplication.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.service.contexts.SecurityContext;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String BEARER_AUTH = "Bearer";


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bkexercise"))
                .build()
                .apiInfo(apiInfo()).securitySchemes(securitySchemes()).securityContexts(List.of(securityContext()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Job Application System", //title
                "API Documentation (BK Exercise)",
                "Version 1.0",
                "Terms of service",
                new Contact("Gilbert TUYISHIME", "https://www.linkedin.com/in/gilbert-tuyishime-5977ab166/", "giltuyishime@gmail.com"),
                "License of API", "API license URL", Collections.emptyList());
    }

    private List<SecurityScheme> securitySchemes() {
        return List.of(new  ApiKey(BEARER_AUTH, "Authorization", "header"));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(List.of(bearerAuthReference())).forPaths(PathSelectors.ant("/api/v1/application/**")).build();
    }

    private SecurityReference bearerAuthReference() {
        return new SecurityReference(BEARER_AUTH, new AuthorizationScope[0]);
    }

}
