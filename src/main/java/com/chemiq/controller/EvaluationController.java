package com.chemiq.controller;

import com.chemiq.DTO.CustomUserDetails;
import com.chemiq.DTO.ErrorResponse;
import com.chemiq.DTO.EvaluationRequest;
import com.chemiq.entity.Evaluation;
import com.chemiq.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "평가 (Evaluation) API", description = "미션 제출물에 대한 평가 관련 API")
@RestController
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Operation(
            summary = "파트너의 미션 제출물 평가하기",
            description = "특정 제출물(submissionId)에 대해 점수(0~5, 0.5단위)와 코멘트를 남깁니다. 자신의 제출물이나 이미 평가된 제출물에는 평가할 수 없습니다.",
            security = @SecurityRequirement(name = "JWT"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "평가 점수와 코멘트를 담은 JSON 객체",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EvaluationRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "평가 생성 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "integer", format = "int64", example = "1"))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 제출물 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "평가할 권한이 없음 (예: 자신의 제출물)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 평가가 완료된 제출물",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (예: 점수 범위 초과)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/submissions/{submissionId}/evaluations")
    public ResponseEntity<?> createEvaluation(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "평가할 제출물의 고유 ID", required = true, example = "1")
            @PathVariable Long submissionId,
            @Valid @RequestBody EvaluationRequest requestDto) {

            Evaluation newEvaluation = evaluationService.createEvaluation(
                    customUserDetails.getMemberNo(),
                    submissionId,
                    requestDto);
            // 201 Created 응답 및 생성된 리소스의 ID 반환
            return ResponseEntity.status(HttpStatus.CREATED).body(newEvaluation.getId());

            // Service 로직에서 ID로 Submission을 찾지 못했을 때 발생하는 예외. 404
            // (예: 클라이언트가 존재하지 않는 submissionId로 요청)

            // Service 로직에서 권한 문제를 확인했을 때 발생하는 예외. 403
            // (예: 자신의 제출물을 평가하려고 시도, 파트너가 아닌 사람이 평가 시도)

            // Service 로직에서 잘못된 '상태'의 요청을 확인했을 때 발생하는 예외. 409
            // (예: 이미 평가가 완료된 제출물에 다시 평가를 시도)

            // Service 로직에서 잘못된 '입력 값'을 확인했을 때 발생하는 예외. 404
            // (예: 점수가 0~5점 범위를 벗어나거나, 0.5 단위가 아닐 경우)
    }
}