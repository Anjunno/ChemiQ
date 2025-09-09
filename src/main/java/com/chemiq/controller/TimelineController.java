package com.chemiq.controller;

import com.chemiq.DTO.CustomUserDetails;
import com.chemiq.DTO.DailyMissionResponse;
import com.chemiq.DTO.ErrorResponse;
import com.chemiq.DTO.TimelineResponse;
import com.chemiq.service.TimelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "타임라인 (Timeline) API", description = "파트너와 공유하는 미션 기록 조회")
@RestController
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @Operation(
            summary = "공유 타임라인 조회 (페이징)",
            description = "로그인된 사용자와 파트너가 제출한 모든 미션 기록을 페이징하여 최신순으로 조회합니다. 파트너가 없거나 제출 기록이 없으면 빈 페이지를 반환합니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "타임라인 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DailyMissionResponse.class))) 
            }
    )
    @GetMapping("/timeline")
    public ResponseEntity<Page<DailyMissionResponse>> getTimeline(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "20") int size
    ) {
        Page<DailyMissionResponse> timelinePage = timelineService.getTimeline(customUserDetails.getMemberNo(), page, size);
        return ResponseEntity.ok(timelinePage);
    }

}
