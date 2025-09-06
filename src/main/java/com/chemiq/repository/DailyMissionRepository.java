package com.chemiq.repository;

import com.chemiq.entity.DailyMission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {
}
