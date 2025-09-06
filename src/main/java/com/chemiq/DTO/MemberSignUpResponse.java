package com.chemiq.DTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "회원가입 응답 DTO")
public class MemberSignUpResponse {
    @Schema(description = "회원가입 응답 메시지")
    private String message;

    // 필요 시 추가 필드
    // private Long memberId;
    // private String token;
}
