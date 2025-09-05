package com.emolink.emolink.service;

import com.emolink.emolink.DTO.PartnershipPartnerResponse;
import com.emolink.emolink.DTO.PartnershipReceiveResponse;
import com.emolink.emolink.DTO.PartnershipSentResponse;
import com.emolink.emolink.entity.Member;
import com.emolink.emolink.entity.Partnership;
import com.emolink.emolink.entity.PartnershipStatus;
import com.emolink.emolink.exception.MemberNotFoundException;
import com.emolink.emolink.repository.MemberRepository;
import com.emolink.emolink.repository.PartnershipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnershipService {
    private final PartnershipRepository partnershipRepository;
    private final MemberRepository memberRepository;

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


        // 3. 요청자 또는 수신자가 이미 다른 사람과 파트너 관계(ACCEPTED)인지 먼저 확인
        List<Long> memberNosToCheck = List.of(requesterNo, addressee.getMemberNo());
        if (partnershipRepository.existsAcceptedPartnershipForMembers(memberNosToCheck)) {
            throw new IllegalStateException("요청자 또는 수신자가 이미 파트너가 있는 사용자입니다.");
        }


        // 4. 두 사용자 사이의 기존 관계를 조회. (PENDING, CANCELED, REJECTED)
        Optional<Partnership> existingPartnershipOpt = partnershipRepository.findPartnershipBetween(requester, addressee);

        if (existingPartnershipOpt.isPresent()) {
            Partnership existingPartnership = existingPartnershipOpt.get();
            PartnershipStatus status = existingPartnership.getStatus();

            // ACCEPTED 상태는 이미 위에서 걸렀으므로, PENDING 상태만 확인하면 됨
            if (status == PartnershipStatus.PENDING) {
                if (existingPartnership.getRequester().equals(requester)) {
                    throw new IllegalStateException("이미 파트너 요청을 보낸 상대입니다.");
                } else {
                    throw new IllegalStateException("상대방이 이미 당신에게 파트너 요청을 보냈습니다. 요청을 확인해주세요.");
                }
            }

            // CANCELED 또는 REJECTED 상태라면, 기존 row를 재활용하여 업데이트
            if (status == PartnershipStatus.CANCELED || status == PartnershipStatus.REJECTED) {
                existingPartnership.setRequester(requester);
                existingPartnership.setAddressee(addressee);
                existingPartnership.setStatus(PartnershipStatus.PENDING);
                return existingPartnership;
            }
        }

        // 5. 기존 관계가 전혀 없는 경우에만 새로 생성
        Partnership newPartnership = Partnership.builder()
                .requester(requester)
                .addressee(addressee)
                .build();

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
    public void acceptPartnership(Long partnershipId, Long acceptingMemberNo) {

        // 1. requestId를 사용하여 PENDING 상태인 파트너십 요청 확인.
        Partnership partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> new EntityNotFoundException("해당 파트너 요청을 찾을 수 없습니다."));

        // 2. 요청의 상태가 PENDING이 맞는지 확인.
        if (partnership.getStatus() != PartnershipStatus.PENDING) {
            throw new IllegalStateException("이미 처리되었거나 유효하지 않은 요청입니다.");
        }

        // 3. 요청을 수락하려는 사용자(acceptingMemberNo)가 해당 요청의 수신자(addressee)가 맞는지 확인. (보안)
        if (!partnership.getAddressee().getMemberNo().equals(acceptingMemberNo)) {
            throw new AccessDeniedException("요청을 수락할 권한이 없습니다.");
        }

        Member requester = partnership.getRequester();

        // 4. 요청자와 수신자가 그 사이에 다른 사람과 파트너가 되었는지 확인 (경쟁 상태 방지)
        List<Long> memberNosToCheck = List.of(requester.getMemberNo(), acceptingMemberNo);
        if (partnershipRepository.existsAcceptedPartnershipForMembers(memberNosToCheck)) {
            throw new IllegalStateException("요청자 또는 수신자가 이미 다른 파트너와 연결되었습니다.");
        }

        // 5. 모든 검증을 통과하면, 상태를 ACCEPTED로 변경.
        partnership.setStatus(PartnershipStatus.ACCEPTED);
    }

    @Transactional
    public void rejectPartnership(Long partnershipId, Long rejectingMemberNo) {
        // 1. partnershipId로 해당 요청을 찾음
        Partnership partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> new EntityNotFoundException("해당 파트너 요청을 찾을 수 없습니다."));

        // 2. 요청의 상태가 PENDING이 맞는지 확인.
        if (partnership.getStatus() != PartnershipStatus.PENDING) {
            throw new IllegalStateException("이미 처리되었거나 유효하지 않은 요청입니다.");
        }
        // 2. 요청을 거절하는 사람이 수신자가 맞는지 확인
        if (!partnership.getAddressee().getMemberNo().equals(rejectingMemberNo)) {
            throw new AccessDeniedException("요청을 거절할 권한이 없습니다.");
        }
        // 3. status를 REJECTED로 변경
        partnership.setStatus(PartnershipStatus.REJECTED);
    }

    @Transactional(readOnly = true)
    public List<PartnershipReceiveResponse> searchReciveList(Long memberNo) {

        // DB에서 Entity 리스트를 조회
        List<Partnership> pendingList = partnershipRepository.findByAddressee_MemberNoAndStatus(memberNo, PartnershipStatus.PENDING);

        // Entity 리스트를 DTO 리스트로 변환하여 반환
        return pendingList.stream()
                .map(PartnershipReceiveResponse::new) // .map(p -> new PartnershipReceiveResponse(p))와 동일
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartnershipSentResponse> searchSentList(Long memberNo) {

        // DB에서 Entity 리스트를 조회
        List<Partnership> pendingList = partnershipRepository.findByRequester_MemberNo(memberNo);

        // Entity 리스트를 DTO 리스트로 변환하여 반환
        return pendingList.stream()
                .map(PartnershipSentResponse::new)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public PartnershipPartnerResponse findPartnerInfo(Long memberNo) {

        // DB에서 memberNo가 속한 파트너 관계 조회
        Partnership partnership = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자와 파트너를 관계를 맺은 사용자가 없습니다."));

        // 내가(memberNo) 요청자(requester)이면, 상대방(addressee)을 파트너로 반환.
        // 내가 요청자가 아니면, 나는 수신자(addressee)이므로 요청자(requester)를 파트너로 반환.
        Member partner = partnership.getRequester().getMemberNo().equals(memberNo)
                ? partnership.getAddressee()
                : partnership.getRequester();

        return new PartnershipPartnerResponse(partner);

    }
}