package com.emolink.emolink.repository;

import com.emolink.emolink.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
