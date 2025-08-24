package com.emolink.emolink.controller;


import com.emolink.emolink.jwt.JWTUtil;
import com.emolink.emolink.repository.RefreshRepository;
import com.emolink.emolink.service.ReissueService;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final ReissueService reissueService;



    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody Map<String, String> payload, HttpServletRequest request, HttpServletResponse response) {

        //요청 body에서 refresh token 추출
        String refreshToken = payload.get("refreshToken");

        try {
            // access, refresh token 재발급
            Map<String, String> newTokens = reissueService.reissueToken(refreshToken);

            // 1. 응답 Body에 담을 새로운 Refresh Token 준비
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("newRefreshToken", newTokens.get("newRefreshToken"));

            // 2. ResponseEntity 빌더를 사용하여 Header와 Body를 함께 구성하여 반환
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + newTokens.get("newAccessToken"))
                    .body(responseBody);

        } catch(IllegalArgumentException e) {
            // 요청 body에 refresh token이 없거나 토큰 유형이 refresh가 아님
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch(ExpiredJwtException e) {
            // 토큰 만료됨
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }












    }
}
