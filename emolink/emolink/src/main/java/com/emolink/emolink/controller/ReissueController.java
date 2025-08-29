package com.emolink.emolink.controller;


import com.emolink.emolink.DTO.*;
import com.emolink.emolink.jwt.JWTUtil;
import com.emolink.emolink.service.ReissueService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "토큰 재발급 API", description = "refresh token을 통해 새로운 access, refresh token 발급")
@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;


    @Operation(
            summary = "토큰 재발급 요청",
            description = "유효한 Refresh Token을 사용하여 Access Token, Refresh Token을 재발급합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh Token을 포함한 JSON 객체",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReissueRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                            content = @Content(schema = @Schema(implementation = ReissueResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: Refresh Token 누락)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (예: 만료되거나 유효하지 않은 Refresh Token)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody ReissueRequest reissueRequest) {

        //요청 body에서 refresh token 추출
        String refreshToken = reissueRequest.getRefreshToken();

        try {
            // access, refresh token 재발급
            TokenDto newTokens = reissueService.reissueToken(refreshToken);

            // 1. 응답 Body에 담을 새로운 Refresh Token 준비
            ReissueResponse responseBody = new ReissueResponse(newTokens.getRefreshToken());

            // 2. ResponseEntity 빌더를 사용하여 Header와 Body를 함께 구성하여 반환
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + newTokens.getAccessToken())
                    .body(responseBody);

        } catch(IllegalArgumentException e) {
            // 요청 body에 refresh token이 없거나 토큰 유형이 refresh가 아님
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch(ExpiredJwtException e) {
            // 토큰 만료됨
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Refresh token이 만료되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
