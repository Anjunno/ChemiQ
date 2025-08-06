package com.emolink.emolink.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.w3c.dom.ls.LSOutput;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {


    // username이 아닌 memeberId롤 찾을 수 있게 오버라이드
    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("memberId");
    }

    private final AuthenticationManager authenticationManager;

//    public LoginFilter(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //memberId, password 추출하기
        String memberId = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println(memberId);
        System.out.println(password);

        //스프링 시큐리티에서 아이디, 비번 검증을 위해 Token에 담음 (아이디, 비번, 권한)
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(memberId, password, null);

        //AuthenticationManager에 토큰 전달
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication) {


        System.out.println("로그인 성공");

    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) {


        System.out.println("로그인 실패");
    }

}
