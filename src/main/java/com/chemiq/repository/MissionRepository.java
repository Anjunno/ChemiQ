package com.chemiq.repository;

import com.chemiq.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    // DB에 저장된 모든 Mission 중 하나를 랜덤으로 가져오는 쿼리
    @Query(value = "SELECT * FROM mission ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Mission> findRandomMission();

}
