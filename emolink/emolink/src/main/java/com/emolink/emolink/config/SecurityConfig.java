package com.emolink.emolink.config;

import com.emolink.emolink.jwt.JWTUtil;
import com.emolink.emolink.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    // AuthenticationManager가 인자로 받음 AuthenticationConfiguration 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 비활성화 (POST 테스트 가능)
                .csrf(csrf -> csrf.disable())

                // 로그인 폼 비활성화
                .formLogin(form -> form.disable())

                //httpBasic 인증방식 비활성화
                .httpBasic((auth) -> auth.disable())

                //JWT 방식임 -> 세션 = stateless 방식으로 관리함 (중요)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //경로별 인가 작업
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signUp").permitAll() // 회원가입은 인증 없이 허용
                        .anyRequest().permitAll()               // 다른 요청도 허용 (나중에 수정 가능)
//                        .anyRequest().authenticated()               // 다른 요청 인증된 사용자
                );

        http // UsernamePasswordAuthenticationFilter 자리에 LoginFilter로 대체
                .addFilterAt(new LoginFilter(jwtUtil, authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
