package com.emolink.emolink.repository;

import com.emolink.emolink.entity.Member;
import com.emolink.emolink.entity.Partnership;
import com.emolink.emolink.entity.PartnershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnershipRepository extends JpaRepository<Partnership, Long> {
    // 특정 회원이 requester 또는 addressee로 참여하고, 상태가 ACCEPTED인 파트너십이 있는지 확인
    boolean existsByStatusAndRequester_MemberNoOrStatusAndAddressee_MemberNo(
            PartnershipStatus status1, Long requesterNo,
            PartnershipStatus status2, Long addresseeNo
    );

    // 특정 요청자와 수신자 사이에 PENDING 상태의 요청이 있는지 확인
    boolean existsByRequesterAndAddresseeAndStatus(
            Member requester, Member addressee, PartnershipStatus status
    );


    @Query("SELECT p FROM Partnership p WHERE " +
            "(p.requester = :userA AND p.addressee = :userB) OR " +
            "(p.requester = :userB AND p.addressee = :userA)")
    Optional<Partnership> findPartnershipBetween(
            @Param("userA") Member userA,
            @Param("userB") Member userB
    );


    //memberNo로 ACCEPTED 상태인 파트너십 엔티티를 직접 조회하는 메소드
    @Query("SELECT p FROM Partnership p WHERE p.status = 'ACCEPTED' AND " +
            "(p.requester.memberNo = :memberNo OR p.addressee.memberNo = :memberNo)")
    Optional<Partnership> findAcceptedPartnershipByMemberNo(@Param("memberNo") Long memberNo);
}
