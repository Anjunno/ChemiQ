//package com.chemiq.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Builder
//@Getter
//@Setter
//@Entity
//@NoArgsConstructor  // 기본 생성자 추가 (public)
//@AllArgsConstructor // 전체 필드 생성자 추가
//public class Device {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long deviceId;
//
//    @Column(nullable = false, unique = true)
//    private String deviceUuid;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_no", nullable = false)
//    private Member member;
//
//}
