package com.project.studylink.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {

    private static final String SCHEME_NAME = "JWT";

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("스터디링크 API")
                .description("스터디링크 API 명세서입니다.")
                .version("v1.0.0");


        Components components = new Components().addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
                .name(HttpHeaders.AUTHORIZATION)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
        );

        return new OpenAPI()
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(components);
    }

    @Bean
    public GroupedOpenApi groupV1() {
        return GroupedOpenApi.builder()
                .group("api v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}