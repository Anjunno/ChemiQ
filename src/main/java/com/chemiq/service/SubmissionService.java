package com.chemiq.service;

import com.chemiq.DTO.PresignedUrlRequest;
import com.chemiq.DTO.PresignedUrlResponse;
import com.chemiq.DTO.SubmissionCreateRequest;
import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Member;
import com.chemiq.entity.Partnership;
import com.chemiq.entity.Submission;
import com.chemiq.repository.DailyMissionRepository;
import com.chemiq.repository.MemberRepository;
import com.chemiq.repository.PartnershipRepository;
import com.chemiq.repository.SubmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final S3Service s3Service;
    private final SubmissionRepository submissionRepository;
    private final MemberRepository memberRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final PartnershipRepository partnershipRepository;


    @Transactional(readOnly = true)
    public PresignedUrlResponse generateUploadUrl(Long memberNo, PresignedUrlRequest requestDto) {

        // 1. 파트너가 존재하는지 확인
        Partnership partnership = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo)
                .orElseThrow(() -> new IllegalStateException("파트너가 존재하지 않아 미션을 제출할 수 없습니다."));

        // 2. 오늘 날짜의 미션이 할당되었는지 확인
        DailyMission dailyMission = dailyMissionRepository.findByPartnershipAndMissionDate(partnership, LocalDate.now())
                .orElseThrow(() -> new IllegalStateException("오늘 할당된 미션이 없습니다."));

        // 3. 이미 오늘 미션을 제출했는지 확인
        Member submitter = memberRepository.getReferenceById(memberNo); // memberNo로 Member 참조 가져오기
        if (submissionRepository.existsByDailyMissionAndSubmitter(dailyMission, submitter)) {
            throw new IllegalStateException("이미 오늘 미션을 제출했습니다.");
        }

        // 4. 모든 검증 통과 시, S3 서비스에 URL 생성 요청
        return s3Service.getUploadPresignedUrl(requestDto.getFilename());
    }

    @Transactional
    public Submission createSubmission(Long memberNo, SubmissionCreateRequest requestDto) {
        // 1. DailyMission 존재 여부 확인
        DailyMission dailyMission = dailyMissionRepository.findById(requestDto.getDailyMissionId())
                .orElseThrow(() -> new EntityNotFoundException("ID " + requestDto.getDailyMissionId() + "에 해당하는 미션을 찾을 수 없습니다."));

        // 2. 오늘 미션이 맞는지 확인
        if (!dailyMission.getMissionDate().equals(LocalDate.now())) {
            throw new IllegalStateException("오늘의 미션에만 제출할 수 있습니다.");
        }

        // 3. 제출자가 해당 파트너십의 멤버가 맞는지 확인 (권한 검증)
        Member submitter = memberRepository.getReferenceById(memberNo);
        Long partnershipIdOfMission = dailyMission.getPartnership().getId();

        // Member 엔티티에 Partnership 정보가 없으므로, partnershipId를 통해 직접 비교
        boolean isMemberOfPartnership = partnershipRepository
                .findAcceptedPartnershipByMemberNo(memberNo)
                .map(p -> p.getId().equals(partnershipIdOfMission))
                .orElse(false);

        if (!isMemberOfPartnership) {
            throw new AccessDeniedException("해당 미션을 수행할 권한이 없습니다.");
        }

        // 4. 이미 제출했는지 확인 (중복 제출 방지)
        if (submissionRepository.existsByDailyMissionAndSubmitter(dailyMission, submitter)) {
            throw new IllegalStateException("이미 오늘 미션을 제출했습니다.");
        }

        // 5. 모든 검증 통과 후, Submission 엔티티 생성 및 저장
        Submission submission = Submission.builder()
                .dailyMission(dailyMission)
                .submitter(submitter)
                .content(requestDto.getContent())
                .imageUrl(requestDto.getFileKey()) // URL 대신 파일 키(key)를 저장
                .build();

        return submissionRepository.save(submission);
    }

}
