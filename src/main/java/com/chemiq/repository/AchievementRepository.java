
package com.chemiq.repository;

import com.chemiq.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    // 고유 코드로 Achievement를 찾는 메소드
    Optional<Achievement> findByCode(String code);
}
