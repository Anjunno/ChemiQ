package com.emolink.emolink.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//JWT 토큰을 생성하고, 토큰에서 사용자 정보를 추출하며, 유효성을 검사하는 유틸 클래스
@Component
public class JWTUtil {

    // JWT 서명에 사용될 비밀 키
    private SecretKey secretKey;

    // 생성자
    // application.properties에서 설정한 secret 값을 주입받아 SecretKey 객체 생성
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        // HS256 알고리즘에 맞는 SecretKey 객체 생성
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),  // 문자열 → 바이트 변환
                Jwts.SIG.HS256.key().build().getAlgorithm()  // "HmacSHA256"
        );
    }

    // 토큰에서 사용자 ID(memberId) 추출
    public Long getMemberNo(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)   // 비밀 키로 서명 검증
                .build()
                .parseSignedClaims(token)  // 토큰 파싱
                .getPayload()
                .get("memberNo", Number.class)  // 클레임에서 memberNo 추출
                .longValue();
    }


    // 토큰에서 사용자 ID(memberId) 추출
    public String getMemberId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)   // 비밀 키로 서명 검증
                .build()
                .parseSignedClaims(token)  // 토큰 파싱
                .getPayload()
                .get("memberId", String.class);  // 클레임에서 memberId 추출
    }


    // 토큰에서 사용자 역할(role) 추출
    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);  // 클레임에서 role 추출
    }

    /**
     * 토큰의 만료 여부 확인
     * @return true → 만료됨 / false → 유효함
     */
    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());  // 현재 시간보다 이전이면 만료
    }

    // JWT 토큰 유형 추출
    public String getCategory(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);  // 클레임에서 cartegory 추출
    }

    /**
     * JWT 생성 메서드
     * @param memberId 사용자 고유 ID
     * @param role 사용자 권한(Role)
     * @param expiredMs 토큰 유효 시간 (밀리초 단위)
     * @return 서명된 JWT 문자열 반환
     */
    public String createJwt(String category, Long memberNo, String memberId, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)                                // 토큰 유형 (access, refresh)
                .claim("memberNo", memberNo)
                .claim("memberId", memberId)                                // 사용자 ID 추가
                .claim("role", role)                                        // 사용자 역할 추가
                .issuedAt(new Date(System.currentTimeMillis()))                // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs))  // 만료 시간
                .signWith(secretKey)                                           // 비밀 키로 서명
                .compact();                                                    // JWT 문자열로 압축
    }
}
