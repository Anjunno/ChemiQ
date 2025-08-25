package com.emolink.emolink.jwt;

import com.emolink.emolink.repository.RefreshTokenRepository;
import com.emolink.emolink.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // doFilter 필수로 오버라이드
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();

        // /logout 경로 요청이 아니면 다음 필터 실행
        if(!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        // post 요청이 아니면 다음 필터 실행
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        // refresh token 추출
        // 2. 요청 Body에서 Refresh Token 추출
        String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String, String> bodyMap = objectMapper.readValue(requestBody, Map.class);
        String refreshToken = bodyMap.get("refreshToken");
        System.out.println("로그아웃 중임 : " + refreshToken);


        // refresh token이 null이면 400에러
        if (refreshToken == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        // refresh token 만료 여부 확인 (401에러)
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        //DB에 refresh token 저장되어 있는지 확인 : 이미 로그아웃된 상태인지 확인 (401에러)
        //로그아웃 실행
        try {
            refreshTokenService.logout(refreshToken);
        } catch (IllegalArgumentException e) {
            // 서비스에서 토큰이 없다고 예외를 던진 경우
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 최종 200응답
        response.setStatus(HttpServletResponse.SC_OK);


















    }
}
