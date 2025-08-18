package com.emolink.emolink.repository;

import com.emolink.emolink.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByMemberId(String memberId);

    Optional<Member> findByMemberId(String memberId);

}
