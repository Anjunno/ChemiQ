package com.chemiq.service;

import com.chemiq.DTO.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    // Pre-signed URL 생성을 위한 AWS S3 Presigner 클라이언트
    private final S3Presigner s3Presigner;

    // 설정된 S3 버킷 이름을 주입받음.
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 클라이언트가 S3에 직접 파일을 업로드할 수 있는, 시간제한이 있는 Pre-signed URL을 생성
    public PresignedUrlResponse getUploadPresignedUrl(String filename) {
        //1. 파일 키 생성 (폴더 구조 + UUID + 원본 파일명)
        String fileKey = "submissions/" + UUID.randomUUID().toString() + "-" + filename;

        //2. S3에 파일을 올리는(PutObject) 요청을 미리 준비
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        //3. Pre-signed URL을 생성하기 위한 요청
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // URL 유효 시간 10분
                .putObjectRequest(putObjectRequest)
                .build();

        //4. S3 Presigner를 사용하여 최종적으로 Pre-signed URL을 생성
        String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

        //5. 클라이언트에게 업로드할 URL과 업로드 후 서버에 알려줄 파일 키를 함께 반환
        return new PresignedUrlResponse(presignedUrl, fileKey);
    }
}
