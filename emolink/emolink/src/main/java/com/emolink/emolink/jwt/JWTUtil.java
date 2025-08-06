package com.emolink.emolink.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JWTUtil {
    static final SecretKey secretKey =
            Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(
               "emolinkSuperSecretKeyForJwtSigningMustBeAtLeast256Bits!"
            ));

    public static String createAccessToken() {
        return "accessToken";
    }

    public static String createRefreshToken() {
        return "refreshToken";
    }

    public static String extractToken() {
        return "accessToken";
    }

}
