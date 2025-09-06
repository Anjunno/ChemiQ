package com.chemiq.entity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor  // 기본 생성자 추가 (public)
@AllArgsConstructor // 전체 필드 생성자 추가
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_mission_id", nullable = false)
    private DailyMission dailyMission; // 어떤 '오늘의 미션'에 대한 제출물인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member submitter; // 이 제출물을 올린 사람

    @Column(nullable = false)
    private String imageUrl; // 사용자가 업로드한 사진의 URL

    @Column(length = 1000)
    private String content; // 사진과 함께 작성한 글

    @CreationTimestamp // 엔티티가 처음 저장될 때의 시간이 자동으로 기록됨
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
