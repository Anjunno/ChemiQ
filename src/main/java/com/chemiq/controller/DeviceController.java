package com.chemiq.controller;

import com.chemiq.DTO.CustomUserDetails;
import com.chemiq.DTO.DeviceRegisterResponse;
import com.chemiq.DTO.ErrorResponse;
import com.chemiq.DTO.*;
import com.chemiq.entity.Device;
import com.chemiq.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "기기 관리 API", description = "사용자의 기기(무드등)를 등록하고 관리합니다.")
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(
            summary = "사용자 기기 등록",
            description = "로그인된 사용자의 계정에 새로운 무드등 기기를 등록하고, 기기의 고유 UUID를 발급합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "기기 등록 성공",
                    content = @Content(schema = @Schema(implementation = DeviceRegisterResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 기기가 존재함",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
//    @SecurityRequirement(name = "JWT")
    @PostMapping("/device/register")
    public ResponseEntity<?> registerDeviceUuid(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            Long memberNo = customUserDetails.getMemberNo();
            Device device = deviceService.registerNewDevice(memberNo);
            String uuid = device.getDeviceUuid();

            // 생성된 리소스의 위치를 나타내는 URI 생성
            URI location = URI.create("/api/devices/" + device.getDeviceId());
            // 201 Created 상태 코드로 응답
            return ResponseEntity.status(HttpStatus.CREATED).body(new DeviceRegisterResponse(uuid));

        } catch (IllegalStateException e) {
            // 중복 등록 예외를 별도로 처리하여 409 Conflict 응답
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(errorResponse);
        }
    }
}
