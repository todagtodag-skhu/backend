package kr.omong.todagtodag.domain.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({OAuthProperties.class, JwtProperties.class})
public class AuthPropertiesConfig {
}
