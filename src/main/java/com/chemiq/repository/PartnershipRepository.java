package com.chemiq.repository;

import com.chemiq.entity.Member;
import com.chemiq.entity.Partnership;
import com.chemiq.entity.PartnershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PartnershipRepository extends JpaRepository<Partnership, Long> {

    // 특정 사용자가 포함된 모든 PENDING 상태의 요청을 찾는 쿼리
    @Query("SELECT p FROM Partnership p WHERE p.status = 'PENDING' AND " +
            "(p.requester = :member OR p.addressee = :member)")
    List<Partnership> findAllPendingRequestsInvolvingMember(@Param("member") Member member);


    // 특정 회원이 requester 또는 addressee로 참여하고, 상태가 ACCEPTED인 파트너십이 있는지 확인
    boolean existsByStatusAndRequester_MemberNoOrStatusAndAddressee_MemberNo(
            PartnershipStatus status1, Long requesterNo,
            PartnershipStatus status2, Long addresseeNo
    );

    // 특정 요청자와 수신자 사이에 PENDING 상태의 요청이 있는지 확인
    boolean existsByRequesterAndAddresseeAndStatus(
            Member requester, Member addressee, PartnershipStatus status
    );


    // 파트너 상태 확인
    @Query("SELECT count(p) > 0 FROM Partnership p WHERE " +
            "p.status = com.chemiq.entity.PartnershipStatus.ACCEPTED AND " + // 1. 상태를 직접 명시
            "((p.requester = :userA AND p.addressee = :userB) OR " +       // 2. A가 요청자, B가 수신자 이거나
            "(p.requester = :userB AND p.addressee = :userA))")            //    B가 요청자, A가 수신자인 경우
    boolean existsAcceptedPartnershipBetween(@Param("userA") Member userA, @Param("userB") Member userB);

    @Query("SELECT p FROM Partnership p WHERE " +
            "(p.requester = :memberA AND p.addressee = :memberB) OR " +
            "(p.requester = :memberB AND p.addressee = :memberA)")
    Optional<Partnership> findPartnershipBetween(
            @Param("memberA") Member memberA,
            @Param("memberB") Member memberB
    );

    // 특정 상태의 상태인 모든 파트너십 목록을 조회
    List<Partnership> findAllByStatus(PartnershipStatus status);


    //memberNo로 ACCEPTED 상태인 파트너십 엔티티를 직접 조회하는 메소드
    @Query("SELECT p FROM Partnership p WHERE p.status = 'ACCEPTED' AND " +
            "(p.requester.memberNo = :memberNo OR p.addressee.memberNo = :memberNo)")
    Optional<Partnership> findAcceptedPartnershipByMemberNo(@Param("memberNo") Long memberNo);



    //memberNo와 partnershipId로 요청자가 memberNo이고 PENDING 상태인 파트너십 엔티티를 직접 조회하는 메소드
//    @Query("SELECT p FROM Partnership p WHERE p.status = 'PENDING' AND " +
//            "(p.requester.memberNo = :memberNo AND p.id = :partnershipId)")
//    Optional<Partnership> findPendingPartnershipByMemberNoAndPartnershipId(
//            @Param("partnershipId") Long partnershipId,
//            @Param("memberNo") Long memberNo);

    Optional<Partnership> findByIdAndRequester_MemberNoAndStatus(Long id, Long memberNo, PartnershipStatus status);

    // 여러 명의 memberNo 중에서 ACCEPTED 상태인 파트너십이 하나라도 있는지 확인
    @Query("SELECT count(p) > 0 FROM Partnership p WHERE p.status = 'ACCEPTED' AND " +
            "(p.requester.memberNo IN :memberNos OR p.addressee.memberNo IN :memberNos)")
    boolean existsAcceptedPartnershipForMembers(@Param("memberNos") List<Long> memberNos);


//    @Query("SELECT p FROM Partnership p WHERE p.status = 'PENDING' AND p.addressee = :memberNo")
//    Optional<List<Partnership>> findAllPendingByAddresseeNo(@Param("memberNo") Long memberNo);

    // addressee의 memberNo가 일치하고, status가 일치하는 Partnership을 모두 조회하는 메서드
    List<Partnership> findByAddressee_MemberNoAndStatus(Long memberNo, PartnershipStatus status);

//    // requester의 memberNo가 일치하고, status가 일치하는 Partnership을 모두 조회하는 메서드
//    List<Partnership> findByRequester_MemberNoAndStatus(Long memberNo, PartnershipStatus status);

    // requester의 memberNo가 일치한 Partnership을 모두 조회하는 메서드
    List<Partnership> findByRequester_MemberNo(Long memberNo);
}
