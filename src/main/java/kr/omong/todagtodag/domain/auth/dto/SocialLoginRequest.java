package kr.omong.todagtodag.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SocialLoginRequest(
        @NotBlank String token
) {
}
