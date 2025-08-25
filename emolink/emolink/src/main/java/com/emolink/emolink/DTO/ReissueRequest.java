package com.emolink.emolink.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Schema(description = "토큰 재발급 요청 DTO")
public class ReissueRequest {
    @Schema(description = "기존 refresh token")
    private String refreshToken;
}
