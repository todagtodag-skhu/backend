package kr.omong.todagtodag.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.oauth.apple")
public record OAuthProperties(
        String keyUrl,
        String clientId
) {
}
