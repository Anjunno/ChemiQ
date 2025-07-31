package com.emolink.emolink.controller;

import com.emolink.emolink.DTO.MemberSignUpRequest;
import com.emolink.emolink.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody MemberSignUpRequest memberSignUpRequest) {
        System.out.println("회원가입 요청 들어옴");
        if(memberService.createMember(memberSignUpRequest)) {
            return ResponseEntity.ok("회원가입 성공");
        }
        return ResponseEntity.badRequest().body("회원가입 실패");
    }
}
