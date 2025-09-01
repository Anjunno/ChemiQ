package com.emolink.emolink.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "기기등록 응답 DTO")
public class DeviceRegisterResponse {

    @Schema(description = "발급된 기기 UUID")
    private String deviceUuid;
}
