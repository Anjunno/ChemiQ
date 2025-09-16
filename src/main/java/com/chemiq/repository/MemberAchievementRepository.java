package com.chemiq.repository;

import com.chemiq.entity.Achievement;
import com.chemiq.entity.Member;
import com.chemiq.entity.MemberAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAchievementRepository extends JpaRepository<MemberAchievement, Long> {

    // 특정 사용자가 특정 도전과제를 이미 달성했는지 확인하는 메소드
    boolean existsByMemberAndAchievement(Member member, Achievement achievement);

    boolean existsByMemberAndAchievement_Code(Member member, String achievementCode);

    // 특정 회원이 달성한 모든 도전과제를 Achievement 정보와 함께 조회
    @Query("SELECT ma FROM MemberAchievement ma JOIN FETCH ma.achievement WHERE ma.member = :member ORDER BY ma.earnedAt DESC")
    List<MemberAchievement> findAllByMemberWithAchievement(@Param("member") Member member);
}
