package com.chemiq.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member; // 도전과제를 달성한 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement; // 달성한 도전과제

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime earnedAt; // 달성한 시간

    @Builder
    public MemberAchievement(Member member, Achievement achievement) {
        this.member = member;
        this.achievement = achievement;
    }
}