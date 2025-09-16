package com.chemiq.repository;

import com.chemiq.entity.Achievement;
import com.chemiq.entity.Member;
import com.chemiq.entity.MemberAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAchievementRepository extends JpaRepository<MemberAchievement, Long> {

    // 특정 사용자가 특정 도전과제를 이미 달성했는지 확인하는 메소드
    boolean existsByMemberAndAchievement(Member member, Achievement achievement);

    boolean existsByMemberAndAchievement_Code(Member member, String achievementCode);
}
