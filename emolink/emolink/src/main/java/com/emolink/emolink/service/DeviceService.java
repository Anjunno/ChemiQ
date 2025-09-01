package com.emolink.emolink.service;

import com.emolink.emolink.entity.Device;
import com.emolink.emolink.entity.Member;
import com.emolink.emolink.repository.DeviceRepository;
import com.emolink.emolink.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Device registerNewDevice(Long memberNo) {
        // 1. 사용자 정보 가져와 객체 생성
        Member member = memberRepository.getReferenceById(memberNo);

        // 2. 해당 유저가 이미 기기를 등록했는지 확인하는 로직 추가 가능
        if (deviceRepository.existsByMember_MemberNo(memberNo)) {
            throw new IllegalStateException("이미 등록된 기기가 있습니다.");
        }

        // 3. 랜덤값(uuid) 생성
        String uuid = UUID.randomUUID().toString();
        // 4. DB 저장을 위한 device 객체 생성
        Device device = Device.builder()
                .deviceUuid(uuid)
                .member(member)
                .build();

        return deviceRepository.save(device);
    }
}
