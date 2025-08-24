package com.emolink.emolink.jwt;

import com.emolink.emolink.DTO.CustomUserDetails;
import com.emolink.emolink.entity.Member;
import com.emolink.emolink.entity.RefreshToken;
import com.emolink.emolink.repository.RefreshRepository;
import com.emolink.emolink.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {


    // username이 아닌 memeberId롤 찾을 수 있게 오버라이드
    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("memberId");
    }

    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshRepository refreshRepository;
//    private final MemberService memberService;

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
            HttpServletRequest request,     //클라이언트가 보낸 요청 객체
            HttpServletResponse response,   //서버가 클라이언트에게 응답할 객체
            FilterChain chain,              //필터 체인 (다음 필터로 넘길 수 있음)
            Authentication authentication   //인증 정보를 담은 객체 (사용자 정보 포함)
    ) throws IOException {
        // 인증된 사용자 정보 가져오기
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // 사용자 No 추출 (memberNo)
        Long memberNo = customUserDetails.getMemberNo();

        // 사용자 ID 추출 (memberId)
        String memberId = customUserDetails.getUsername();

        // 사용자 권한(ROLE_XXX) 가져오기
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        // 권한 이름 추출 (ex. "ROLE_USER", "ROLE_ADMIN")
        String role = auth.getAuthority();

        // JWT 엑세스토큰 생성 (만료 시간: 1시간)
        String accessToken = jwtUtil.createJwt("access", memberNo, memberId, role, 60*60 * 1000L);

        // JWT 리프레시토큰 생성 (만료 시간: 10시간)
        String refreshToken = jwtUtil.createJwt("refresh", memberNo, memberId, role, 60*60*10 * 1000L);


        // JWT 리프레시토큰 DB에 저장
        saveRefreshToken(memberNo, refreshToken, 60*60*10 * 1000L);



        // 응답 헤더에 JWT 토큰 추가 (Bearer 타입으로 명시)
        response.addHeader("Authorization", "Bearer " + accessToken);



        // RefreshToken → 바디(JSON)
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 응답 바디에 담을 데이터를 Map 구조로 생성
        Map<String, String> tokens = new HashMap<>();
        tokens.put("refreshToken", refreshToken);

        // ObjectMapper를 사용해 Map을 JSON 문자열로 변환 후, response 바디에 씀
        new ObjectMapper().writeValue(response.getWriter(), tokens);

    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) {


//        System.out.println("로그인 실패");
        response.setStatus(401);
    }


    // refresh token DB 저장 메서드
    private void saveRefreshToken(Long memberNo, String refreshToken, Long expiredMs)  {

        // Member 엔티티를 프록시(참조)로 가져옴.
        Member member = Member.builder().memberNo(memberNo).build();

        // 만료시간
        Date expiration = new Date(System.currentTimeMillis() + expiredMs);

        // 객체 생성
        RefreshToken token = RefreshToken.builder()
                .member(member)
                .refreshToken(refreshToken)
                .expiration(expiration)
                .build();

        // DB에 저장
        refreshRepository.save(token);
    }
}
