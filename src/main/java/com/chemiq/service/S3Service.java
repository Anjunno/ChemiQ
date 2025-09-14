package com.chemiq.service;

import com.chemiq.DTO.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAclRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    // Pre-signed URL 생성을 위한 AWS S3 Presigner 클라이언트
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    // 설정된 S3 버킷 이름을 주입받음.
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 클라이언트가 S3에 직접 파일을 업로드할 수 있는, 시간제한이 있는 Pre-signed URL을 생성
    public PresignedUrlResponse getUploadPresignedUrl(String folder, String filename) {
        //1. 파일 키 생성 (폴더 구조 + UUID + 원본 파일명)
        String fileKey = folder + "/" + UUID.randomUUID().toString() + "-" + filename;

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

    public String getDownloadPresignedUrl(String fileKey) {
        // filekey 없는 경우
        if (fileKey == null || fileKey.isBlank()) {
            return null;
        }
        //1. S3에 파일을 다운로드할(GetObject) 요청을 미리 준비
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();
        //2. S3 Presigner를 사용하여 최종적으로 Pre-signed URL을 생성
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();

        //3. url 반환
        return presignedUrl;
    }

    public void deleteFile(String fileKey) {

        // 1. [방어 로직] fileKey가 유효한지 확인합니다.
        //    (null이거나 비어있으면 삭제할 대상이 없으므로 아무것도 하지 않고 종료)
        if (fileKey == null || fileKey.isBlank()) {
            log.warn("삭제할 파일 키가 비어있습니다.");
            return;
        }

        try {
            // 2. S3에서 파일을 삭제하기 위한 요청 객체를 생성합니다.
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileKey)
                    .build();

            // 3. S3 클라이언트를 통해 삭제 요청을 보냅니다.
            s3Client.deleteObject(deleteObjectRequest);

            log.info("S3 파일 삭제 성공: {}", fileKey);

        } catch (Exception e) {
            // S3 통신 중 에러가 발생하더라도, DB 작업은 롤백되지 않아야 하므로
            // 여기서 예외를 던지는 대신 로그만 남기고 넘어가는 것이 더 안정적일 수 있습니다.
            log.error("S3 파일 삭제 중 오류가 발생했습니다. Key: {}", fileKey, e);
        }
    }
}
