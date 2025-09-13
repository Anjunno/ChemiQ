package com.chemiq.entity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor  // 기본 생성자 추가 (public)
@AllArgsConstructor // 전체 필드 생성자 추가
public class DailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnership_id", nullable = false)
    private Partnership partnership; // 이 미션을 받은 커플

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission; // 할당된 미션의 내용

    @Column(nullable = false)
    private LocalDate missionDate; // 이 미션이 할당된 날짜

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DailyMissionStatus status = DailyMissionStatus.ASSIGNED;

}
