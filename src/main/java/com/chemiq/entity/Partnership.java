package com.chemiq.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
