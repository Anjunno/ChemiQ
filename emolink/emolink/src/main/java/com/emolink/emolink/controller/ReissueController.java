package com.emolink.emolink.controller;


import com.emolink.emolink.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody Map<String, String> payload, HttpServletRequest request, HttpServletResponse response) {

        //요청 body에서 refresh token 추출
        String refreshToken = payload.get("refreshToken");

        // 1. 요청 body에 refreshToken 필드 확인
        if(refreshToken == null) { //요청 body에 refreshToken 필드 없음
            return new ResponseEntity<>("refresh token 필드 없음", HttpStatus.BAD_REQUEST);
        }

        // 2. 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch(ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token 만료", HttpStatus.BAD_REQUEST);
        }

        // 3. 토큰 유형이 refresh가 맞는지 확인
        String category = jwtUtil.getCategory(refreshToken);
        if(!category.equals("refresh")) {
            return new ResponseEntity<>("refresh token 아님", HttpStatus.BAD_REQUEST);
        }

        /* ------------------------ 올바른 refresh token 확인함 ----------------------- */

        // JWT 토큰 생성을 위해 정보 추출
        String memberId = jwtUtil.getMemberId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        //새로운 access token 생성
        String newAccessToken = jwtUtil.createJwt("access", memberId, role, 60*60L);

        // 응답 헤더에 JWT 토큰 추가 (Bearer 타입으로 명시)
        response.addHeader("Authorization", "Bearer " + newAccessToken);
        return new ResponseEntity<>(HttpStatus.OK);


    }
}
