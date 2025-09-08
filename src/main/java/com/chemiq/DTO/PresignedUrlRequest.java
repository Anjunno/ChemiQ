package com.chemiq.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Pre-Signed URL 발급 요청 DTO")
public class PresignedUrlRequest {
    private String filename; // 클라이언트가 업로드할 파일의 원본 이름 (예: "sky.jpg")
}
