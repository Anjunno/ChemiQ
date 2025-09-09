package com.chemiq.controller;

import com.chemiq.DTO.*;
import com.chemiq.entity.Submission;
import com.chemiq.service.S3Service;
import com.chemiq.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "미션 제출 (Submission) API", description = "미션 수행 결과(사진, 글) 제출 관련 API")
public class SubmissionController {

    private final SubmissionService submissionService;


    @Operation(
            summary = "[1단계] 미션 사진 업로드용 URL 요청",
            description = "미션 수행 결과로 사진을 업로드하기 위한, 10분간 유효한 일회성 업로드 전용 URL(Pre-signed URL)을 발급받습니다.",
            security = @SecurityRequirement(name = "JWT"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업로드할 원본 파일의 이름을 담은 JSON",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PresignedUrlRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "URL 발급 성공",
                            content = @Content(schema = @Schema(implementation = PresignedUrlResponse.class))),
                    @ApiResponse(responseCode = "409", description = "업로드 자격 없음 (파트너 없음, 오늘 미션 없음, 이미 제출함)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "S3 통신 오류 등 서버 내부 문제",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    /**
     * [1단계] 이미지 업로드를 위한 Pre-signed URL 발급 API
     * 클라이언트가 S3에 직접 파일을 올릴 수 있도록, 시간제한이 있는 임시 URL을 발급합니다.
     */
    @PostMapping("/submissions/presigned-url")
    public ResponseEntity<?> getUploadUrl(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody PresignedUrlRequest requestDto) { // @Valid로 DTO 유효성 검증

            //SubmissionService 호출
            PresignedUrlResponse response = submissionService.generateUploadUrl(
                    customUserDetails.getMemberNo(),
                    requestDto
            );

            return ResponseEntity.ok(response);

            // "파트너 없음", "미션 없음", "이미 제출함" 등의 예외 처리 409

            // S3와의 통신 중 예상치 못한 오류(예: AWS 서비스 장애, 자격 증명 문제)가 발생할 수 있습니다. 500

    }



    @Operation(
            summary = "[2단계] 미션 제출 완료 보고",
            description = "Pre-signed URL을 통해 S3에 사진 업로드를 완료한 후, 해당 정보와 글 내용을 서버에 최종 기록합니다.",
            security = @SecurityRequirement(name = "JWT"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "미션 ID, 글 내용, S3 파일 키를 담은 JSON",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SubmissionCreateRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "미션 제출 최종 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 미션 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "해당 미션을 제출할 권한이 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 제출했거나 오늘 미션이 아님",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    /**
     * [2단계] 미션 제출 완료 보고 API
     * 클라이언트가 S3에 파일 업로드를 완료한 후, 해당 정보(파일 키, 글 내용 등)를
     * 서버에 최종 전달하여 데이터베이스에 기록합니다.
     */
    @PostMapping("/submissions")
    public ResponseEntity<?> createSubmission(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody SubmissionCreateRequest requestDto) {


            Submission newSubmission = submissionService.createSubmission(customUserDetails.getMemberNo(), requestDto);
            // 성공 시, 생성된 Submission의 ID를 반환하고 201 Created 상태 코드를 응답.
            return ResponseEntity.status(HttpStatus.CREATED).body(newSubmission.getId());


            // 요청한 dailyMissionId가 존재하지 않을 때 (404 Not Found)

            // 해당 미션을 제출할 권한이 없을 때 (403 Forbidden)

            // 이미 제출했거나, 오늘 미션이 아닐 때 (409 Conflict)

    }
}