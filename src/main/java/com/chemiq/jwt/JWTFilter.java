package com.chemiq.jwt;

import com.chemiq.DTO.CustomUserDetails;
import com.chemiq.entity.Member;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

// 요청에 대해 한 번만 동작하는 필터(OncePerRequestFilter) 상속 -> JWT 토큰 검증을 위해 사용
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    // 필터 내부 구현 (doFilterInternal() 메서드 필수로 구현해야함)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String path = request.getRequestURI();
        // 회원가입, 로그인 등 토큰 검사 불필요한 경로는 필터 무시
//        if (path.equals("/signup") || path.equals("/login") || path.equals("/reissue"))
        if (path.equals("/signup")
                || path.equals("/login")
                || path.equals("/reissue")
                || path.startsWith("/api/internal/submissions/image-key")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/webjars/"))
        {
            filterChain.doFilter(request, response);
            return;
        }

        //----------------------JWT 토큰 검증 ----------------------------------//

        String authorization = request.getHeader("Authorization");

        // 1. Authorization 헤더에 토큰 존재 유무 검증
        // 요청 헤더의 Authorization 값이 없거나 Bearer로 시작하지 않음
        if(authorization == null || !authorization.startsWith("Bearer")) {

            PrintWriter writer = response.getWriter();
            writer.print("token null");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // stateCode : 401
            return;

//            System.out.println("token Null");
//            // 토큰 없음 → 다음 필터로 넘어감 (로그인 안 된 상태)
//            filterChain.doFilter(request, response);
//
//            return;
        }


        // "Bearer" 제거한 토큰 값 추출 (Authorization헤더 값이 "Bearer abcdefg123123" 이러한 형식)
        String accessToken = authorization.split(" ")[1];



        // 2. 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken); //토큰이 만료되었으면 ExpiredJwtException 발생
        }catch(ExpiredJwtException e) {

            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // stateCode : 401
            return;
        }


        // 3. access token 검증
        // access token인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);
        //access token이 아니라면
        if(!category.equals("access")) {

            PrintWriter writer = response.getWriter();
            writer.print("invaild access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // stateCode : 401
            return;
        }

        


        // 토큰에서 사용자의 memberNo, memberId, role 획득
        Long memberNo = jwtUtil.getMemberNo(accessToken);
        String memberId = jwtUtil.getMemberId(accessToken);
        String role = jwtUtil.getRole(accessToken);

        Member member = Member.builder()
                .memberNo(memberNo)
                .memberId(memberId)               // JWT에서 꺼낸, 인증된 사용자를 정확히 식별하는 ID
                .password("임의 비밀번호")          // 실제 인증에 사용되지 않으므로 임의로 설정해도 무방
                .nickname("임의 닉네임")            // 인증에 직접 영향 없는 값이라 임의로 넣어도 무방
                .role(role)                      // JWT에서 꺼낸 권한 정보로, 인가 처리에 사용됨
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails,              // 인증된 사용자 정보 (principal)
                null,                          // 비밀번호는 JWT 인증이므로 필요하지 않아 null 처리
                customUserDetails.getAuthorities() // 사용자 권한 목록
        );

        SecurityContextHolder.getContext().setAuthentication(authToken); // 현재 쓰레드의 보안 컨텍스트에 인증 정보 저장

        filterChain.doFilter(request,response);



    }
}
