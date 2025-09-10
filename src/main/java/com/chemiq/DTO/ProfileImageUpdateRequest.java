package com.chemiq.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class ProfileImageUpdateRequest {
    @NotBlank(message = "파일 키는 필수입니다.")
    private String fileKey;
}
