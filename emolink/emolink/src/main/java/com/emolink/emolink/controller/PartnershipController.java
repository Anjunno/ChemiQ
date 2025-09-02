package com.emolink.emolink.controller;

import com.emolink.emolink.DTO.CustomUserDetails;
import com.emolink.emolink.DTO.ErrorResponse;
import com.emolink.emolink.DTO.PartnershipRequest;
import com.emolink.emolink.entity.Partnership;
import com.emolink.emolink.exception.MemberNotFoundException;
import com.emolink.emolink.service.PartnershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "파트너십 (Partnership) API", description = "파트너 요청, 수락 등 커플 연결 관련 API")
public class PartnershipController {

    private final PartnershipService partnershipService;
    @Operation(
            summary = "파트너 요청 보내기",
            description = "로그인된 사용자가 다른 사용자에게 파트너 관계를 요청합니다.",
            security = @SecurityRequirement(name = "JWT"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "파트너로 요청할 사용자의 ID를 담은 JSON 객체",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartnershipRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "파트너 요청 성공"),
                    @ApiResponse(responseCode = "404", description = "요청한 상대방 사용자를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 파트너 관계이거나 처리 대기중인 요청이 존재함",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 자기 자신에게 요청)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    //파트너 요청
    @PostMapping("/partnership/request")
    public ResponseEntity<?> createPartnershipRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @RequestBody PartnershipRequest partnershipRequest) {
        try{
            Long requesterNo = customUserDetails.getMemberNo();
            String addresseeId = partnershipRequest.getPartnerId();
            Partnership newPartnershipRequest = partnershipService.createRequest(requesterNo, addresseeId);

            // 성공 시: 201 Created 응답
            URI location = URI.create("/partnership/request/" + newPartnershipRequest.getId());
            return ResponseEntity.created(location).body("파트너 신청이 완료되었습니다.");

        } catch (MemberNotFoundException e) {
            // 실패 1: 상대방 사용자를 찾을 수 없음 (404 Not Found)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (IllegalStateException e) {
            // 실패 2: 이미 파트너가 있거나 요청이 존재함 (409 Conflict)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

        } catch (IllegalArgumentException e) {
            // 실패 3: 자기 자신에게 요청하는 등 잘못된 인자로 요청 (400 Bad Request)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @Operation(
            summary = "파트너 관계 해제",
            description = "현재 로그인된 사용자의 파트너 관계를 해제합니다. 성공 시 관계의 상태는 'CANCELED'로 변경됩니다.",
            security = @SecurityRequirement(name = "JWT"), // 이 API가 JWT 인증을 필요로 함을 명시
            responses = {
                    @ApiResponse(responseCode = "200", description = "파트너 관계 해제 성공"),
                    @ApiResponse(responseCode = "409", description = "해제할 파트너 관계가 존재하지 않음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @DeleteMapping("/partnership")
    // 파트너 관계 해제
    public ResponseEntity<?> deletePartnership(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            Long memberNo = customUserDetails.getMemberNo();
            partnershipService.cancelPartnership(memberNo);

            return ResponseEntity.ok().body("파트너 관계가 해제되었습니다.");

        } catch (IllegalStateException e) {
            // 서비스에서 "파트너가 없다"는 예외를 던진 경우 (409 Conflict)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

    }
}
