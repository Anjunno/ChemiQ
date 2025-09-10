package com.chemiq.service;

import com.chemiq.DTO.*;
import com.chemiq.entity.Member;
import com.chemiq.entity.Partnership;
import com.chemiq.exception.DuplicateMemberIdException;
import com.chemiq.repository.MemberRepository;
import com.chemiq.repository.PartnershipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PartnershipRepository partnershipRepository;

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

    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(Long memberNo) {

        // 1. 내 정보 조회
        Member me = memberRepository.findById(memberNo)
                .orElseThrow(() -> new EntityNotFoundException(memberNo + "에 해당하는 사용자를 찾을 수 없습니다"));

        // 2. 파트너십 정보 조회
        Optional<Partnership> partnershipOpt = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo);

        // 3. 파트너가 있는 경우와 없는 경우를 분기하여 DTO 생성
        if (partnershipOpt.isPresent()) {
            // --- 파트너가 있는 경우
            Partnership partnership = partnershipOpt.get();
            Member partner = me.getMemberNo().equals(partnership.getRequester().getMemberNo())
                    ? partnership.getAddressee()
                    : partnership.getRequester();

            return MyPageResponse.builder()
                    .myInfo(new MemberInfoDto(me))
                    .partnerInfo(new MemberInfoDto(partner))
                    .partnershipInfo(new PartnershipInfoDto(partnership))
                    .build();
        } else {
            // --- 파트너가 없는 경우
            return MyPageResponse.builder()
                    .myInfo(new MemberInfoDto(me))
                    // partnerInfo와 partnershipInfo는 null로 남겨둠 (Builder의 기본값)
                    .build();
        }
    }

    @Transactional
    public void patchNickname(Long memberNo, String newNickname) {

        // 1. 내 정보 조회
        Member me = memberRepository.findById(memberNo)
                .orElseThrow(() -> new EntityNotFoundException(memberNo + "에 해당하는 사용자를 찾을 수 없습니다"));
        // 2. 새로운 닉네임으로 변경 후 저장
        me.changeNickname(newNickname);
    }

    @Transactional
    public void patchPassword(Long memberNo, PasswordChangeRequest request) {

        // 1. 내 정보 조회
        Member me = memberRepository.findById(memberNo)
                .orElseThrow(() -> new EntityNotFoundException(memberNo + "에 해당하는 사용자를 찾을 수 없습니다"));

        // 2. DB에 저장된 비밀번호와 일치하는지 확인
        if(!bCryptPasswordEncoder.matches(request.getPassword(), me.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 현재 비밀번호와 새 비밀번호가 동일한지 확인
        if(bCryptPasswordEncoder.matches(request.getNewPassword(), me.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 4. 새로운 비밀번호 저장
        String encodePassword = bCryptPasswordEncoder.encode(request.getNewPassword());
        me.changePassword(encodePassword);
    }
}
