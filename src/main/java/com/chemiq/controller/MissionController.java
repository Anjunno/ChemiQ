package com.chemiq.controller;

import com.chemiq.DTO.CustomUserDetails;
import com.chemiq.DTO.ErrorResponse;
import com.chemiq.DTO.TodayMissionResponse;
import com.chemiq.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "미션 (Mission) API", description = "오늘의 미션 조회, 미션 수행 제출 등")
@RestController
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;



    @Operation(
            summary = "오늘의 미션 조회",
            description = "로그인된 사용자와 파트너에게 오늘 할당된 미션의 내용을 조회합니다. 만약 파트너 관계가 아니거나, 오늘 할당된 미션이 없다면 404 에러가 반환됩니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "오늘의 미션 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TodayMissionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "파트너 관계가 아니거나 오늘 할당된 미션이 없음",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    // 오늘의 미션 조회
    @GetMapping("/missions/today")
    public ResponseEntity<?> getTodayMission(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        try {
            TodayMissionResponse todayMission = missionService.getTodayMission(customUserDetails.getMemberNo());
            return ResponseEntity.ok(todayMission);

        } catch(EntityNotFoundException e) {
            // 해당 요청을 찾을 수 없음 404
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

    }
}
