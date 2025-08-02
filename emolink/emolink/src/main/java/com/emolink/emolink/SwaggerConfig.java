package com.emolink.emolink;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI emolinkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Emolink API 문서")
                        .description("감정 공유 무드등 서비스 API 명세서")
                        .version("v1.0.0"));
    }
}
