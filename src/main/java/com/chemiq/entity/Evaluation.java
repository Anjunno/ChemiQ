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
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission; // 어떤 제출물에 대한 평가인지 (1:1 관계)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_member_no", nullable = false)
    private Member evaluator; // 누가 평가를 남겼는지

    @Column(nullable = false)
    private double score; // 1.0점 ~ 5.0점

    @Column(length = 500)
    private String comment; // 파트너가 남긴 코멘트

    @CreationTimestamp
    private LocalDateTime createdAt;
}
