package com.chemiq.entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor  // 기본 생성자 추가 (public)
@AllArgsConstructor // 전체 필드 생성자 추가
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    @NotBlank // null, "", " " 모두 허용하지 않음
    @Size(min = 5, max = 12) // 애플리케이션 레벨에서 5~12자 길이 검증
    @Column(length = 12, nullable = false, unique = true)
    private String memberId;

    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(min = 2, max = 6)
    @Column(length = 6, nullable = false)
    private String nickname;

//    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
//    private Device device;

    private String role;

//    private String refreshToken;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;
}
