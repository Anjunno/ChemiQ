package com.chemiq.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
@Schema(description = "토큰 재발급 응답 DTO")
public class ReissueResponse {
    @Schema(description = "새로 발급한 refresh token")
    private String newRefreshToken;
}
