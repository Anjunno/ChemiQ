package com.chemiq.service;

import com.chemiq.DTO.*;
import com.chemiq.entity.DailyMissionStatus;
import com.chemiq.entity.Member;
import com.chemiq.entity.MemberAchievement;
import com.chemiq.entity.Partnership;
import com.chemiq.exception.DuplicateMemberIdException;
import com.chemiq.repository.DailyMissionRepository;
import com.chemiq.repository.MemberAchievementRepository;
import com.chemiq.repository.MemberRepository;
import com.chemiq.repository.PartnershipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PartnershipRepository partnershipRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final S3Service s3Service;

    @Transactional
    public Member createMember(MemberSignUpRequest memberSignUpRequest) {

        // 1. 아이디 중복 체크
        if (memberRepository.existsByMemberId(memberSignUpRequest.getMemberId())) {
            // DuplicateMemberIdException 예외를 발생.
            throw new DuplicateMemberIdException("이미 사용 중인 아이디입니다.");
        }

        // 닉네임 중복 체크 추가.
//        if (memberRepository.existsByNickname(memberSignUpRequest.getNickname())) {
//            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
//        }

        // 2. 기본 역할을 ROLE_USER로 설정.
        String role = "ROLE_USER";

        Member newMember = Member.builder()
                .memberId(memberSignUpRequest.getMemberId())
                .password(bCryptPasswordEncoder.encode(memberSignUpRequest.getPassword()))
                .nickname(memberSignUpRequest.getNickname())
                .role(role)
                .build();

        // 3. 성공 시, 생성된 Member 객체를 반환합니다.
        return memberRepository.save(newMember);
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(Long memberNo) {

        // 1. 내 정보 조회
        Member me = memberRepository.findById(memberNo)
                .orElseThrow(() -> new EntityNotFoundException(memberNo + "에 해당하는 사용자를 찾을 수 없습니다"));

        // 내 프로필 이미지에 대한 Pre-signed URL 생성
        String myProfileImageUrl = s3Service.getDownloadPresignedUrl(me.getProfileImageKey());
        // 수정된 생성자를 사용하여 DTO 생성
        MemberInfoDto myInfoDto = new MemberInfoDto(me, myProfileImageUrl);

        // 내 도전과제 목록 조회 및 DTO 변환
        List<MemberAchievement> achievements = memberAchievementRepository.findAllByMemberWithAchievement(me);
        List<AchievementDto> achievementDtos = achievements.stream()
                .map(AchievementDto::new)
                .collect(Collectors.toList());

        // 2. 파트너십 정보 조회
        Optional<Partnership> partnershipOpt = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo);

        // 3. 파트너가 있는 경우와 없는 경우를 분기하여 DTO 생성
        if (partnershipOpt.isPresent()) {
            // --- 파트너가 있는 경우 ---
            Partnership partnership = partnershipOpt.get();
            Member partner = me.getMemberNo().equals(partnership.getRequester().getMemberNo())
                    ? partnership.getAddressee()
                    : partnership.getRequester();

            //  파트너 프로필 이미지에 대한 Pre-signed URL 생성
            String partnerProfileImageUrl = s3Service.getDownloadPresignedUrl(partner.getProfileImageKey());

            // 수정된 생성자를 사용하여 DTO 생성
            MemberInfoDto partnerInfoDto = new MemberInfoDto(partner, partnerProfileImageUrl);

            long totalMissions = dailyMissionRepository.countByPartnershipAndStatus(
                    partnership, DailyMissionStatus.COMPLETED);

            // 2. 이번 주 완료 미션 개수 조회
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            long weeklyMissions = dailyMissionRepository.countByPartnershipAndStatusAndMissionDateBetween(
                    partnership, DailyMissionStatus.COMPLETED, startOfWeek, endOfWeek);

            // 수정된 생성자로 PartnershipInfoDto 생성
            PartnershipInfoDto partnershipInfoDto = new PartnershipInfoDto(partnership, totalMissions, weeklyMissions);

            return MyPageResponse.builder()
                    .myInfo(myInfoDto)
                    .partnerInfo(partnerInfoDto)
                    .partnershipInfo(partnershipInfoDto)
                    .myAchievements(achievementDtos)
                    .build();
        } else {
            // --- 파트너가 없는 경우 ---
            return MyPageResponse.builder()
                    .myInfo(myInfoDto)
                    .myAchievements(achievementDtos)
                    .build();
        }
    }

    @Transactional
    public void patchNickname(Long memberNo, String newNickname) {

        // 1. 내 정보 조회
        Member me = memberRepository.findById(memberNo)
                .orElseThrow(() -> new EntityNotFoundException(memberNo + "에 해당하는 사용자를 찾을 수 없습니다"));
        // 2. 새로운 닉네임으로 변경 후 저장
        me.changeNickname(newNickname);
    }

    @Transactional
    public void patchPassword(Long memberNo, PasswordChangeRequest request) {

        // 1. 내 정보 조회
        Member me = memberRepository.findById(memberNo)
                .orElseThrow(() -> new EntityNotFoundException(memberNo + "에 해당하는 사용자를 찾을 수 없습니다"));

        // 2. DB에 저장된 비밀번호와 일치하는지 확인
        if(!bCryptPasswordEncoder.matches(request.getPassword(), me.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 현재 비밀번호와 새 비밀번호가 동일한지 확인
        if(bCryptPasswordEncoder.matches(request.getNewPassword(), me.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 4. 새로운 비밀번호 저장
        String encodePassword = bCryptPasswordEncoder.encode(request.getNewPassword());
        me.changePassword(encodePassword);
    }

    @Transactional(readOnly = true)
    public PresignedUrlResponse generateProfileImageUploadUrl(PresignedUrlRequest requestDto) {
        // "profiles" 폴더에 저장하도록 S3 서비스 호출
        return s3Service.getUploadPresignedUrl("profiles", requestDto.getFilename());
    }

    @Transactional
    public void updateProfileImage(Long memberNo, String fileKey) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 기존 프로필 이미지가 있었다면 S3에서 삭제
        String oldImageKey = member.getProfileImageKey();
        if (oldImageKey != null && !oldImageKey.isBlank()) {
            s3Service.deleteFile(oldImageKey);
        }

        member.changeProfileImageKey(fileKey); // Dirty Checking으로 자동 업데이트
    }
}
