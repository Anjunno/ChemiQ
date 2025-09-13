package com.chemiq.service;

import com.chemiq.DTO.PartnershipReceiveResponse;
import com.chemiq.DTO.PartnershipSentResponse;
import com.chemiq.DTO.PartnershipPartnerResponse;
import com.chemiq.entity.Member;
import com.chemiq.entity.Partnership;
import com.chemiq.entity.PartnershipStatus;
import com.chemiq.exception.MemberNotFoundException;
import com.chemiq.repository.MemberRepository;
import com.chemiq.repository.PartnershipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnershipService {
    private final PartnershipRepository partnershipRepository;
    private final MemberRepository memberRepository;

    private static final Logger log = LoggerFactory.getLogger(PartnershipService.class);
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


        // --- 경쟁 상태(Race Condition) 방지 로직 ---
        Member requester = partnership.getRequester();
        Member addressee = partnership.getAddressee(); // acceptingMemberNo에 해당하는 Member 객체

        List<Long> memberNosToCheck = List.of(requester.getMemberNo(), addressee.getMemberNo());
        if (partnershipRepository.existsAcceptedPartnershipForMembers(memberNosToCheck)) {
            throw new IllegalStateException("요청자 또는 수락자가 그 사이에 이미 다른 파트너와 연결되었습니다.");
        }

        // 4. 상태를 ACCEPTED로 변경
        partnership.setStatus(PartnershipStatus.ACCEPTED);
        partnership.setAcceptedAt(LocalDate.now());

        // 5. 관련된 다른 모든 PENDING 요청 정리
        log.info("파트너십 ID {} 수락됨. 관련 PENDING 요청 정리를 시작합니다.", partnership.getId());

        // 요청자와 수신자가 연관된 모든 PENDING 요청을 조회
        List<Partnership> requesterPendingRequests = partnershipRepository.findAllPendingRequestsInvolvingMember(requester);
        List<Partnership> addresseePendingRequests = partnershipRepository.findAllPendingRequestsInvolvingMember(addressee);

        //두 리스트를 합치고, 방금 수락된 요청(자기 자신)은 제외
        Set<Partnership> requestsToReject = new HashSet<>();
        requestsToReject.addAll(requesterPendingRequests);
        requestsToReject.addAll(addresseePendingRequests);
        requestsToReject.remove(partnership); // 방금 수락된 요청은 처리 대상에서 제외

        //나머지 모든 PENDING 요청들의 상태를 REJECTED로 변경
        requestsToReject.forEach(request -> request.setStatus(PartnershipStatus.REJECTED));

        log.info("{}개의 관련 파트너십 요청이 자동으로 거절 처리되었습니다.", requestsToReject.size());


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

        // 1. 조회할 상태 목록을 정의.
        List<PartnershipStatus> statusesToFind = List.of(
                PartnershipStatus.PENDING,
                PartnershipStatus.REJECTED
        );

        // 2. PENDING과 REJECTED 상태의 요청만 DB에서 조회.
        List<Partnership> sentList = partnershipRepository.findByRequester_MemberNoAndStatusIn(memberNo, statusesToFind);

        // 3. Entity 리스트를 DTO 리스트로 변환.
        return sentList.stream()
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


    @Transactional
    public void cancelRequest(Long partnershipId, Long memberNo) {
        // 1. partnershipId로 파트너십 요청이 존재하는지 먼저 확인.
        Partnership partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> new EntityNotFoundException("ID가 " + partnershipId + "인 파트너 요청을 찾을 수 없습니다."));

        // 2. 로그인한 사용자가 해당 요청의 주인(requester)이 맞는지 확인.
        if (!partnership.getRequester().getMemberNo().equals(memberNo)) {
            throw new AccessDeniedException("해당 요청을 취소할 권한이 없습니다.");
        }

        // 3. 해당 요청이 PENDING 상태가 맞는지 확인.
        if (partnership.getStatus() != PartnershipStatus.PENDING) {
            throw new IllegalStateException("이미 취소되었거나 거절된 요청입니다.");
        }

        // 4. 모든 검증을 통과하면, 상태를 CANCELED로 변경.
        partnership.setStatus(PartnershipStatus.CANCELED);
    }
}