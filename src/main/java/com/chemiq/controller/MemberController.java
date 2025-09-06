package com.chemiq.controller;

import com.chemiq.DTO.ErrorResponse;
import com.chemiq.DTO.MemberSignUpResponse;
import com.chemiq.DTO.MemberSignUpRequest;
import com.chemiq.entity.Member;
import com.chemiq.exception.DuplicateMemberIdException;
import com.chemiq.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        try {
            // 서비스는 성공 시 Member 객체를 반환
            Member newMember = memberService.createMember(request);

            // 1. 생성된 리소스의 URI 생성
            URI location = URI.create("/api/members/" + newMember.getMemberNo()); // 예시 경로

            // 2. 201 Created 응답 반환
            return ResponseEntity.created(location)
                    .body(new MemberSignUpResponse("회원가입 성공"));

//            return ResponseEntity.ok(new MemberSignUpResponse("회원가입 성공"));

        } catch (DuplicateMemberIdException e) {
            // 3. 아이디 중복 예외 처리 (409 Conflict)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(errorResponse);

        } catch (IllegalArgumentException e) {
            // 4. 기타 유효성 검증 예외 처리 (400 Bad Request)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);
        }
    }




    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("접근 가능합니다");
    }
}
