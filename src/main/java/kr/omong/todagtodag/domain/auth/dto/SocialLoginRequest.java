package kr.omong.todagtodag.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank String token
) {
}
