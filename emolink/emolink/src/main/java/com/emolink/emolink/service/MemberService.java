package com.emolink.emolink.service;

import com.emolink.emolink.DTO.MemberSignUpRequest;
import com.emolink.emolink.entity.Member;
import com.emolink.emolink.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

//    @Transactional
//    public void addRefresh(String memberId, String refreshToken) {
//
//        // 1. Optional과 orElseThrow로 사용자 조회 (없으면 예외 발생)
//        Member member = memberRepository.findByMemberId(memberId)
//                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다: " + memberId));
//
//        // 2. Refresh Token 해싱
//        String hashedRefreshToken = bCryptPasswordEncoder.encode(refreshToken);
//
//        // 3. Dirty Checking을 통해 토큰 업데이트 (save 호출 없이도 DB에 반영됨)
//        member.setRefreshToken(hashedRefreshToken);
//
//        // 명시적으로 save를 호출해도 문제는 없지만, @Transactional 환경에서는 생략 가능
//        // memberRepository.save(member);
//    }
}
