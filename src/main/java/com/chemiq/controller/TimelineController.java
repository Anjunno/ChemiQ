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


    @Operation(
            summary = "오늘의 미션 현황 조회",
            description = "로그인된 사용자의 오늘 미션 내용과, 나와 파트너의 제출 현황을 함께 조회합니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = DailyMissionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "파트너 관계가 아니거나 오늘 할당된 미션이 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/timeline/today")
    public ResponseEntity<?> getTodayTimeline(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        var todayTimeline = timelineService.getTodayMissionStatus(customUserDetails.getMemberNo());
        return ResponseEntity.ok(todayTimeline);
    }

}
