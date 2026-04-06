package kr.omong.todagtodag.domain.auth.service;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenGenerator {

    private static final int REFRESH_TOKEN_BYTE_LENGTH = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    public String createRefreshToken() {
        byte[] randomBytes = new byte[REFRESH_TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
