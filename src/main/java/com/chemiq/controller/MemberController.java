package com.chemiq.controller;

import com.chemiq.DTO.*;
import com.chemiq.entity.Member;
import com.chemiq.exception.DuplicateMemberIdException;
import com.chemiq.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원관련 API", description = "회원가입, 로그인 등 사용자 관련 기능 제공")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원가입 요청",
            description = "memberId, password nickname를 요청 body로 받아 회원가입 진행",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberSignUpRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = MemberSignUpResponse.class))),
                    @ApiResponse(responseCode = "409", description = "아이디 중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "회원가입 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody MemberSignUpRequest request) {
            // 서비스는 성공 시 Member 객체를 반환
            Member newMember = memberService.createMember(request);

            // 1. 생성된 리소스의 URI 생성
            URI location = URI.create("/api/members/" + newMember.getMemberNo()); // 예시 경로

            // 2. 201 Created 응답 반환
            return ResponseEntity.created(location)
                    .body(new MemberSignUpResponse("회원가입 성공"));


            // 3. 아이디 중복 예외 처리 (409 Conflict)

            // 4. 기타 유효성 검증 예외 처리 (400 Bad Request)
    }




    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("접근 가능합니다");
    }


    @Operation(
            summary = "마이페이지 정보 조회",
            description = "로그인된 사용자의 '마이페이지' 화면에 필요한 모든 정보를 한번에 조회합니다. 파트너가 있는 경우 파트너 정보와 파트너십 정보(스트릭, 케미 지수)가 함께 반환되며, 파트너가 없는 경우 해당 필드들은 null로 반환됩니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "마이페이지 정보 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MyPageResponse.class))),
                    @ApiResponse(responseCode = "404", description = "요청한 회원 정보를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/members/me/info")
    public ResponseEntity<MyPageResponse> getMyPageInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MyPageResponse myPageInfo = memberService.getMyPageInfo(customUserDetails.getMemberNo());
        return ResponseEntity.ok(myPageInfo);
    }


    @Operation(
            summary = "내 닉네임 변경",
            description = "현재 로그인된 사용자의 닉네임을 변경합니다. DTO에 정의된 유효성 검증 규칙을 따릅니다.",
            security = @SecurityRequirement(name = "JWT"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NicknameChangeRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/members/me/nickname")
    public ResponseEntity<?> patchNickname(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody NicknameChangeRequest request) {
        memberService.patchNickname(customUserDetails.getMemberNo(), request.getNickname());
        return ResponseEntity.ok("닉네임이 " + request.getNickname() + "로 변경됨.");
    }


    @Operation(
            summary = "내 비밀번호 변경",
            description = "현재 로그인된 사용자의 비밀번호를 변경합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PasswordChangeRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패, 현재 비밀번호 불일치 등)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/members/me/password")
    public ResponseEntity<?> patchPassword(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        memberService.patchPassword(customUserDetails.getMemberNo(), request);
        return ResponseEntity.ok("비밀번호 변경됨.");
    }
}
