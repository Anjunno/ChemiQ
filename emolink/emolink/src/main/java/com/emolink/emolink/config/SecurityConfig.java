package com.emolink.emolink.config;

import com.emolink.emolink.jwt.JWTFilter;
import com.emolink.emolink.jwt.JWTUtil;
import com.emolink.emolink.jwt.LoginFilter;
import com.emolink.emolink.repository.RefreshRepository;
import com.emolink.emolink.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    // AuthenticationManager가 인자로 받음 AuthenticationConfiguration 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshRepository refreshRepository;

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

        //cors 설정
        /*
        기본적으로 브라우저 환경에서 프론트엔드(예: React, Vue, Angular)와
        백엔드(Spring Boot) 가 다른 포트/도메인에서 동작하면 CORS(Cross-Origin Resource Sharing) 문제가 발생

        예: http://localhost:3000 (React) → http://localhost:8080 (Spring Boot) 요청 시
        Same-Origin Policy 때문에 브라우저가 차단함

        따라서 http.cors(...)로 Spring Security가 제공하는 CORS 처리를 활성화하고,
        거기에 CorsConfigurationSource를 등록해 허용할 출처, 메서드, 헤더 등을 명시적으로 지정
         */
//        http
//                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
//
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//
//                        CorsConfiguration configuration = new CorsConfiguration();
//
//                        // http://localhost:3000에서 오는 요청만 허용
//                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//                        // 모든 HTTP 메서드(GET, POST, PUT, DELETE 등) 허용
//                        configuration.setAllowedMethods(Collections.singletonList("*"));
//                        // 쿠키/인증정보(Authorization 헤더 포함)를 요청에 포함할 수 있도록 허용
//                        configuration.setAllowCredentials(true);
//                        //요청 헤더에 어떤 값이 오든 허용
//                        configuration.setAllowedHeaders(Collections.singletonList("*"));
//                        // CORS preflight 요청(OPTIONS 메서드) 결과를 브라우저 캐시에 1시간 동안 저장
//                        configuration.setMaxAge(3600L);
//                        // 응답 헤더 중 브라우저가 접근할 수 있는 헤더를 지정
//                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
//
//                        return configuration;
//                    }
//                })));

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
                        .requestMatchers("/signup").permitAll() // 회원가입은 인증 없이 허용
                        .requestMatchers("/reissue").permitAll()
                        .anyRequest().permitAll()               // 다른 요청도 허용 (나중에 수정 가능)
//                        .anyRequest().authenticated()               // 다른 요청 인증된 사용자
                );

        http // UsernamePasswordAuthenticationFilter 자리에 LoginFilter로 대체
                .addFilterAt(new LoginFilter(jwtUtil, authenticationManager(authenticationConfiguration), refreshRepository), UsernamePasswordAuthenticationFilter.class)
                //LoginFilter 뒤에 JWTFilter 등록
                .addFilterAfter(new JWTFilter(jwtUtil), LoginFilter.class);


        return http.build();
    }
}
