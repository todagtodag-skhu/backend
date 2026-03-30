package kr.omong.todagtodag.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpirationMillSecond,
        long refreshTokenExpirationMillSecond
) {
}
