package com.chemiq.controller;

import com.chemiq.DTO.CustomUserDetails;
import com.chemiq.DTO.ErrorResponse;
import com.chemiq.DTO.HomeSummaryResponse;
import com.chemiq.DTO.MyPageResponse;
import com.chemiq.service.MemberService;
import com.chemiq.service.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "화면별 API (Screen)", description = "특정 화면에 필요한 정보를 종합적으로 제공하는 API")
@RestController
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @Operation(
            summary = "마이페이지 정보 종합 조회",
            description = "로그인된 사용자의 '마이페이지' 화면에 필요한 모든 정보를 한번에 조회합니다. 사용자의 기본 정보, 획득한 도전과제 목록이 항상 포함됩니다. 파트너가 있는 경우 파트너 정보와 파트너십 상세 정보(스트릭, 케미 지수 등)가 함께 반환되며, 파트너가 없는 경우 해당 필드들은 null로 반환됩니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "마이페이지 정보 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MyPageResponse.class))),
                    @ApiResponse(responseCode = "404", description = "요청한 회원 정보를 찾을 수 없음 (유효하지 않은 토큰)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/members/me/info")
    public ResponseEntity<MyPageResponse> getMyPageInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MyPageResponse myPageInfo = screenService.getMyPageInfo(customUserDetails.getMemberNo());
        return ResponseEntity.ok(myPageInfo);
    }


    @Operation(
            summary = "홈 화면 정보 종합 조회",
            description = "앱의 홈 화면을 구성하는 데 필요한 모든 정보(파트너 정보, 주간 미션 현황, 오늘의 미션)를 한 번의 요청으로 반환합니다. 파트너가 없는 경우 파트너 관련 필드들은 null로 반환됩니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "홈 화면 정보 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = HomeSummaryResponse.class))),
                    @ApiResponse(responseCode = "404", description = "요청한 회원 정보를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/home-summary")
    public ResponseEntity<HomeSummaryResponse> getHomeScreenSummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        HomeSummaryResponse homeSummaryResponse = screenService.getHomeScreenSummary(customUserDetails.getMemberNo());
        return ResponseEntity.ok(homeSummaryResponse);

    }
}
