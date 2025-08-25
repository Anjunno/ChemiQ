package com.emolink.emolink.service;

import com.emolink.emolink.DTO.ReissueResponse;
import com.emolink.emolink.DTO.TokenDto;
import com.emolink.emolink.entity.Member;
import com.emolink.emolink.entity.RefreshToken;
import com.emolink.emolink.jwt.JWTUtil;
import com.emolink.emolink.repository.MemberRepository;
import com.emolink.emolink.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenDto reissueToken(String refreshToken) {

        // 1. 요청 body에 refreshToken 필드 확인
        if(refreshToken == null) { //요청 body에 refreshToken 필드 없음
            throw new IllegalArgumentException("refresh token이 없습니다.");
//            return new ResponseEntity<>("refresh token 필드 없음", HttpStatus.BAD_REQUEST);
        }

        // 2. 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch(ExpiredJwtException e) {
            throw e;
//            return new ResponseEntity<>("refresh token 만료", HttpStatus.BAD_REQUEST);
        }

        // 3. 토큰 유형이 refresh가 맞는지 확인
        String category = jwtUtil.getCategory(refreshToken);
        if(!category.equals("refresh")) {
//            return new ResponseEntity<>("refresh token 아님", HttpStatus.BAD_REQUEST);
            throw new IllegalArgumentException("유효하지 않은 Refresh token입니다.");
        }

        // 4. 해당 refresh token이 DB에 존재하는지 확인
        RefreshToken existingToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("DB에 존재하지 않는 Refresh token입니다."));

        /* ------------------------ 올바른 refresh token 확인함 ----------------------- */

        // 기존 Refresh Token DB에서 제거 (토큰 순환)
        refreshTokenRepository.delete(existingToken);

        // JWT 토큰 생성을 위해 정보 추출
        Long memberNo = jwtUtil.getMemberNo(refreshToken);
        String memberId = jwtUtil.getMemberId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);


        //새로운 access token 생성
        String newAccessToken = jwtUtil.createJwt("access", memberNo, memberId, role, 60*60 * 1000L);
        //새로운 refresh token 생성
        String newRefreshToken = jwtUtil.createJwt("refresh", memberNo, memberId, role, 60*60 * 10 * 1000L);

        // 새로운 refresh token 저장
        saveRefreshToken(memberNo, newRefreshToken,60*60 * 10 * 1000L);


//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("newAccessToken", newAccessToken);
//        tokens.put("newRefreshToken", newRefreshToken);

        // 응답 DTO에 담아 전달
        return new TokenDto(newAccessToken, newRefreshToken);
    }

    // refresh token DB 저장 메서드
    private void saveRefreshToken(Long memberNo, String refreshToken, Long expiredMs)  {

        // Member 엔티티를 프록시(참조)로 가져옴.
        Member member = memberRepository.getReferenceById(memberNo);

        // 만료시간
        LocalDateTime expiration = LocalDateTime.now().plus(expiredMs, ChronoUnit.MILLIS);

        // 객체 생성
        RefreshToken token = RefreshToken.builder()
                .member(member)
                .refreshToken(refreshToken)
                .expiration(expiration)
                .build();

        // DB에 저장
        refreshTokenRepository.save(token);
    }

}
