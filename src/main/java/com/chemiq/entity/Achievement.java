package com.chemiq.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 코드에서 도전과제를 식별하기 위한 고유 코드
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    // 사용자에게 보여줄 도전과제의 이름
    @Column(nullable = false, length = 100)
    private String name;

    // 도전과제에 대한 상세 설명
    @Column(length = 500)
    private String description;

    // 도전과제 아이콘 이미지의 URL
//    private String iconUrl;

    @Builder
    public Achievement(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}