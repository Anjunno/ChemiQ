package com.emolink.emolink.service;

import com.emolink.emolink.DTO.MemberSignUpRequest;
import com.emolink.emolink.entity.Member;
import com.emolink.emolink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public boolean createMember(MemberSignUpRequest memberSignUpRequest) {

        Member member =  Member.builder()
                .memberId(memberSignUpRequest.getMemberId())
                .password(memberSignUpRequest.getPassword())
                .nickname(memberSignUpRequest.getNickname())
                .build();

        memberRepository.save(member);
        return true;
    }
}
