package com.chemiq.repository;

import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Partnership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    Optional<DailyMission> findByPartnershipAndMissionDate(Partnership partnership, LocalDate today);
}
