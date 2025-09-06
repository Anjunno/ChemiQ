package com.chemiq.service;

import com.chemiq.entity.RefreshToken;
import com.chemiq.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void logout(String refreshToken) {

        // 1. DB에서 토큰을 직접 조회합니다.
        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or already logged out token"));

        // 2. 조회된 엔티티를 삭제합니다. 이것이 더 효율적이고 안전합니다.
        refreshTokenRepository.delete(tokenEntity);
    }
}
