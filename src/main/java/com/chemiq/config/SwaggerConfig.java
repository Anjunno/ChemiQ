package com.chemiq.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${swagger.server.url}")
    private String swaggerServerUrl;

    @Bean
    public OpenAPI emolinkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChemiQ API 문서")
                        .description(" 1:1 미션 공유 서비스 API 명세서")
                        .version("v1.0.0")
                )
                .servers(List.of(
                   new Server()
                           .url("http://localhost:8080")
                           .description("개발용 서버"),

                   new Server().url(swaggerServerUrl).description("EC2 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                        )
                );
    }
}
