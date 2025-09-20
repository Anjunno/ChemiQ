package com.chemiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Partnership {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 요청을 보낸 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_no")
    private Member requester;

    // 요청을 받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressee_no")
    private Member addressee;

    // 관계의 상태를 저장 (ENUM 타입) 기본값 : PENDING
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PartnershipStatus status = PartnershipStatus.PENDING; // PENDING, ACCEPTED, REJECTED

    // 스트릭 카운트 필드
    @Builder.Default
    private Integer streakCount = 0;

    // 케미 지수 필드
    @Builder.Default
    private Double chemiScore = 0.0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDate acceptedAt;

    //== 비즈니스 로직을 위한 메소드들 ==//

    /**
     * 스트릭 카운트를 1 증가.
     */
    public void increaseStreak() {
        this.streakCount++;
    }

    /**
     * 스트릭 카운트를 0으로 초기화.
     */
    public void resetStreak() {
        this.streakCount = 0;
    }

    public void applyScorePenalty(double penaltyAmount) {
        // 새로운 점수를 계산.
        double newScore = this.chemiScore - penaltyAmount;

        // 새로운 점수가 0보다 작은지 확인하고, 작다면 0으로 고정.
        this.chemiScore = Math.max(0.0, newScore);
    }
    /**
     * 새로운 평균 점수를 기반으로 케미 지수를 업데이트.
     */
    public void updateChemiScore(double newAverageScore) {
        // 케미 지수가 초기값(0.0)인 경우, 첫 점수를 그대로 반영.
        if (this.chemiScore == 0.0) {
            this.chemiScore = newAverageScore;
        } else {
            // 그 이후부터는 기존 점수와 새 점수의 평균으로 업데이트.
            this.chemiScore = (this.chemiScore + newAverageScore) / 2.0;
        }
    }

    public void increaseChemiScoreByCompletion() {
        this.chemiScore = Math.min(100.0, this.chemiScore + 0.2);
    }
}
