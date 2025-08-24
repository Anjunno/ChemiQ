package com.emolink.emolink.controller;

import com.emolink.emolink.DTO.MemberSignUpRequest;
import com.emolink.emolink.DTO.MemberSignUpResponse;
import com.emolink.emolink.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원관련 API", description = "회원가입, 로그인 등 사용자 관련 기능 제공")
public class MemberController {

    private final MemberService memberService;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = MemberSignUpResponse.class))),
            @ApiResponse(responseCode = "400", description = "회원가입 실패", content = @Content(schema = @Schema(implementation = MemberSignUpResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<MemberSignUpResponse> signUp(@RequestBody MemberSignUpRequest request) {

        if (memberService.createMember(request)) {
            return ResponseEntity.ok(new MemberSignUpResponse("회원가입 성공"));
        }

        return ResponseEntity.badRequest().body(new MemberSignUpResponse("회원가입 실패"));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("접근 가능합니다");
    }
}
