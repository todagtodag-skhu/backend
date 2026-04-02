package kr.omong.todagtodag.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record TodakOnboardingRequest(
        @NotBlank
        String inviteCode
) {
}
