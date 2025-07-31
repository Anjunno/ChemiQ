package com.emolink.emolink;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (POST 테스트 가능)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signUp").permitAll() // 회원가입은 인증 없이 허용
                        .anyRequest().permitAll()               // 다른 요청도 허용 (나중에 수정 가능)
                )
                .formLogin(form -> form.disable());         // 로그인 폼 비활성화

        return http.build();
    }
}
