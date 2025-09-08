package com.chemiq.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Pre-Signed URL 발급 응답 DTO")
public class PresignedUrlResponse {
    private String presignedUrl; // S3에 업로드할 수 있는 임시 URL
    private String fileKey;      // S3에 저장될 고유한 파일 이름 (완료 보고 시 필요)
}
