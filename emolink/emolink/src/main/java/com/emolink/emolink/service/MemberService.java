package com.emolink.emolink.service;

import com.emolink.emolink.DTO.MemberSignUpRequest;
import com.emolink.emolink.entity.Member;
import com.emolink.emolink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean createMember(MemberSignUpRequest memberSignUpRequest) {

        //아이디 중복 체크 (아이디가 이미 존재하면 false 리턴)
        if (memberRepository.existsByMemberId(memberSignUpRequest.getMemberId())) {
            return false;
        }

        String memberId = memberSignUpRequest.getMemberId();
        String password = bCryptPasswordEncoder.encode(memberSignUpRequest.getPassword());
        String nickname = memberSignUpRequest.getNickname();
        String role = "ROLE_ADMIN";

        Member member =  Member.builder()
                .memberId(memberId)
                .password(password)
                .nickname(nickname)
                .role(role)
                .build();

        memberRepository.save(member);
        return true;
    }
}
