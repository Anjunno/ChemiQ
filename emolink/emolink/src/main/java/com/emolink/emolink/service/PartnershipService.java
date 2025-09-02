package com.emolink.emolink.service;

import com.emolink.emolink.entity.Member;
import com.emolink.emolink.entity.Partnership;
import com.emolink.emolink.entity.PartnershipStatus;
import com.emolink.emolink.exception.MemberNotFoundException;
import com.emolink.emolink.repository.MemberRepository;
import com.emolink.emolink.repository.PartnershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnershipService {
    private final PartnershipRepository partnershipRepository;
    private final MemberRepository memberRepository;

    // PartnershipService.java

    @Transactional
    public Partnership createRequest(Long requesterNo, String addresseeId) {
        // 1. 요청자와 수신자 엔티티를 모두 가져옵니다.
        Member requester = memberRepository.getReferenceById(requesterNo);
        Member addressee = memberRepository.findByMemberId(addresseeId)
                .orElseThrow(() -> new MemberNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + addresseeId));

        // 2. 자기 자신에게 요청했는지 확인
        if (requester.getMemberNo().equals(addressee.getMemberNo())) {
            throw new IllegalArgumentException("자기 자신에게 파트너 요청을 보낼 수 없습니다.");
        }

        // 3. 두 사용자 사이의 기존 관계를 먼저 조회.
        Optional<Partnership> existingPartnershipOpt = partnershipRepository.findPartnershipBetween(requester, addressee);

        // 만약 관계가 이미 존재한다면
        if (existingPartnershipOpt.isPresent()) {
            Partnership existingPartnership = existingPartnershipOpt.get();
            PartnershipStatus status = existingPartnership.getStatus();

            // 3-1. 이미 파트너이거나 대기중인 요청이 있으면 예외 처리
            if (status == PartnershipStatus.ACCEPTED) {
                throw new IllegalStateException("이미 파트너 관계인 사용자입니다.");
            }
            if (status == PartnershipStatus.PENDING) {
                throw new IllegalStateException("이미 처리 대기중인 파트너 요청이 존재합니다.");
            }

            // 3-2. CANCELED 또는 REJECTED 상태라면, 기존 row를 재활용하여 업데이트
            if (status == PartnershipStatus.CANCELED || status == PartnershipStatus.REJECTED) {
                existingPartnership.setRequester(requester); // 요청자를 현재 요청한 사람으로 다시 설정
                existingPartnership.setAddressee(addressee);
                existingPartnership.setStatus(PartnershipStatus.PENDING); // 상태를 PENDING으로 변경
                return existingPartnership; // save 호출 없이 Dirty Checking으로 업데이트됨
            }
        }

        // 4. 기존 관계가 전혀 없는 경우에만 새로 생성
        Partnership newPartnership = Partnership.builder()
                .requester(requester)
                .addressee(addressee)
                .build(); // status는 기본값 PENDING

        return partnershipRepository.save(newPartnership);
    }
    @Transactional
    public void cancelPartnership(Long memberNo) {

        // 1. Repository에서 ACCEPTED 상태인 파트너십 엔티티를 조회.
        //    만약 존재하지 않으면 예외를 발생.
        Partnership partnership = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo)
                .orElseThrow(() -> new IllegalStateException("현재 파트너 관계가 아니므로 해제할 수 없습니다."));

        // 2. 조회된 엔티티의 상태를 CANCELED로 변경.
        partnership.setStatus(PartnershipStatus.CANCELED);

    }

    @Transactional
    public void acceptPartnershipRequest(Long partnershipId, Long acceptingMemberNo) {
        // 1. partnershipId로 PENDING 상태인 요청을 찾음
        // 2. 요청을 수락하는 사람(acceptingMemberNo)이 해당 요청의 수신자(addressee)가 맞는지 확인
        // 3. 요청자와 수신자 양쪽 모두 다른 사람과 ACCEPTED 상태의 파트너가 아닌지 다시 한번 확인 (Race Condition 방지)
        // 4. 모든 검증 통과 시, status를 ACCEPTED로 변경
    }

    @Transactional
    public void rejectPartnershipRequest(Long partnershipId, Long rejectingMemberNo) {
        // 1. partnershipId로 PENDING 상태인 요청을 찾음
        // 2. 요청을 거절하는 사람이 수신자가 맞는지 확인
        // 3. status를 REJECTED로 변경
    }
}