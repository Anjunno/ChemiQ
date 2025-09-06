package com.chemiq.repository;

import com.chemiq.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByMemberId(String memberId);

    Optional<Member> findByMemberId(String memberId);

}
