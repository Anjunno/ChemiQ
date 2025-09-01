package com.emolink.emolink.repository;

import com.emolink.emolink.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    boolean existsByMember_MemberNo(Long memberNo);

}
