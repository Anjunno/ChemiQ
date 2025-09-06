package com.chemiq.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원관련 API", description = "로그인/로그아웃 등 사용자 인증 관련 API")
@RestController
public class AuthControllerDoc {

    // --- 1. 로그인 API 문서 ---
    @Operation(summary = "사용자 로그인", description = "ID와 비밀번호(form-data)를 사용하여 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    headers = @Header(name = "Authorization", description = "발급된 Access Token (Bearer 타입)"),
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패 (자격 증명 오류)", content = @Content)
    })
    @PostMapping("/login")
    public void fakeLogin(
            @Parameter(description = "사용자 ID", required = true, example = "user123") String memberId,
            @Parameter(description = "비밀번호", required = true, example = "password123") String password
    ) {
        // 이 메소드의 본문은 실행되지 않습니다.
        // 실제 요청은 LoginFilter가 가로채서 처리합니다.
        // 오직 Swagger 문서 생성을 위한 것입니다.
        throw new UnsupportedOperationException("This method is for Swagger documentation only.");
    }


    // --- 2. 로그아웃 API 문서 ---
    @Operation(summary = "사용자 로그아웃", description = "서버에 저장된 Refresh Token을 무효화하여 로그아웃 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (Refresh Token 누락 등)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않거나 만료된 Refresh Token)", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> fakeLogout(@RequestBody LogoutRequest request) {
        // 이 메소드의 본문은 실행되지 않습니다.
        // 실제 요청은 CustomLogoutFilter가 가로채서 처리합니다.
        // 오직 Swagger 문서 생성을 위한 것입니다.
        throw new UnsupportedOperationException("This method is for Swagger documentation only.");
    }

    // --- Swagger 문서용 DTO 클래스들 ---

    @Getter
    @Setter
    @Schema(description = "로그인 성공 시 응답 Body")
    private static class LoginResponse {
        @Schema(description = "발급된 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String refreshToken;
    }

    @Getter
    @Setter
    @Schema(description = "로그아웃 요청 Body")
    private static class LogoutRequest {
        @Schema(description = "무효화할 Refresh Token", requiredMode = Schema.RequiredMode.REQUIRED, example = "eyJhbGciOiJIUzI1NiJ9...")
        private String refreshToken;
    }
}