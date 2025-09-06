package com.chemiq.service;

import com.chemiq.DTO.MemberSignUpRequest;
import com.chemiq.entity.Member;
import com.chemiq.exception.DuplicateMemberIdException;
import com.chemiq.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Member createMember(MemberSignUpRequest memberSignUpRequest) {

        // 1. 아이디 중복 체크
        if (memberRepository.existsByMemberId(memberSignUpRequest.getMemberId())) {
            // DuplicateMemberIdException 예외를 발생.
            throw new DuplicateMemberIdException("이미 사용 중인 아이디입니다.");
        }

        // 닉네임 중복 체크 추가.
//        if (memberRepository.existsByNickname(memberSignUpRequest.getNickname())) {
//            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
//        }

        // 2. 기본 역할을 ROLE_USER로 설정.
        String role = "ROLE_USER";

        Member newMember = Member.builder()
                .memberId(memberSignUpRequest.getMemberId())
                .password(bCryptPasswordEncoder.encode(memberSignUpRequest.getPassword()))
                .nickname(memberSignUpRequest.getNickname())
                .role(role)
                .build();

        // 3. 성공 시, 생성된 Member 객체를 반환합니다.
        return memberRepository.save(newMember);
    }
}
