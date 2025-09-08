package com.chemiq.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Pre-Signed URL 발급완료 전달(요청) DTO")
public class SubmissionCreateRequest {
    private Long dailyMissionId;
    private String content;
    private String fileKey; // URL 발급 시 받았던 고유 파일 키
}
