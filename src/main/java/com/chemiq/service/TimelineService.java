package com.chemiq.service;
import com.chemiq.DTO.DailyMissionResponse;
import com.chemiq.DTO.SubmissionDetailDto;
import com.chemiq.DTO.TimelineResponse;
import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Partnership;
import com.chemiq.entity.Submission;
import com.chemiq.repository.DailyMissionRepository;
import com.chemiq.repository.PartnershipRepository;
import com.chemiq.repository.SubmissionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Service
@RequiredArgsConstructor
public class TimelineService {

    private final PartnershipRepository partnershipRepository;
    private final SubmissionRepository submissionRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final S3Service s3Service; // 이미지 조회를 위한 Pre-signed URL 생성에 필요


    @Transactional(readOnly = true)
    public Page<DailyMissionResponse> getTimeline(Long memberNo, int page, int size) {

        // 1. 파트너십 조회
        Partnership partnership = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo)
                .orElse(null);
        if (partnership == null) {
            return Page.empty(); // 파트너가 없으면 빈 페이지 반환
        }

        // 2. 파트너십에 해당하는 'DailyMission'을 페이징하여 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by("missionDate").descending());
        Page<DailyMission> dailyMissionPage = dailyMissionRepository.findByPartnershipOrderByMissionDateDesc(partnership, pageable);

        // 3. 조회된 DailyMission들에 속한 모든 Submission들을 DB에서 '한 번에' 가져옴 (N+1 문제 해결)
        List<Submission> submissions = submissionRepository.findAllByDailyMissionIn(dailyMissionPage.getContent());

        // 4. Submission들을 DailyMission ID 기준으로 그룹핑하여 쉽게 찾을 수 있도록 Map으로 변환
        Map<Long, List<Submission>> submissionsMap = submissions.stream()
                .collect(Collectors.groupingBy(s -> s.getDailyMission().getId()));

        // 5. Page<DailyMission>을 Page<DailyMissionResponseDto>로 변환
        return dailyMissionPage.map(dailyMission -> {
            // 해당 DailyMission에 대한 제출물 목록을 Map에서 찾음
            List<Submission> missionSubmissions = submissionsMap.getOrDefault(dailyMission.getId(), Collections.emptyList());

            SubmissionDetailDto mySubmissionDto = null;
            SubmissionDetailDto partnerSubmissionDto = null;

            // 제출물 목록을 순회하며 '나의 제출물'과 '파트너의 제출물'을 찾아서 DTO로 만듦
            for (Submission s : missionSubmissions) {
                String presignedUrl = s3Service.getDownloadPresignedUrl(s.getImageUrl());
                if (s.getSubmitter().getMemberNo().equals(memberNo)) {
                    mySubmissionDto = new SubmissionDetailDto(s, presignedUrl);
                } else {
                    partnerSubmissionDto = new SubmissionDetailDto(s, presignedUrl);
                }
            }

            // 최종적으로 하루치 미션 묶음 DTO를 생성하여 반환
            return DailyMissionResponse.builder()
                    .dailyMissionId(dailyMission.getId())
                    .missionTitle(dailyMission.getMission().getTitle())
                    .missionDate(dailyMission.getMissionDate())
                    .mySubmission(mySubmissionDto)
                    .partnerSubmission(partnerSubmissionDto)
                    .build();
        });
    }
}
