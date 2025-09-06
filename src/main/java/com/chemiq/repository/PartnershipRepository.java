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
            "(p.requester = :memberA AND p.addressee = :memberB) OR " +
            "(p.requester = :memberB AND p.addressee = :memberA)")
    Optional<Partnership> findPartnershipBetween(
            @Param("memberA") Member memberA,
            @Param("memberB") Member memberB
    );


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
