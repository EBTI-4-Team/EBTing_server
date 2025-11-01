package com.github.ebtingserver.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI().components(new Components().addSecuritySchemes("JWT", securityScheme()))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .info(info());
    }

    private Info info() {

        return new Info().title("EBTing").description("EBTI 기반 팀 빌딩 에이전트").version("0.1.0");
    }

    private SecurityScheme securityScheme() {

        return new SecurityScheme().type(HTTP).bearerFormat("JWT").scheme("bearer");
    }
}
