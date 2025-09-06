package com.chemiq.entity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor  // 기본 생성자 추가 (public)
@AllArgsConstructor // 전체 필드 생성자 추가
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title; // 미션 제목

    @Column(length = 500)
    private String description; //미션에 대한 설명
}
