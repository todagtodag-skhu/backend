package kr.omong.todagtodag.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import kr.omong.todagtodag.domain.user.entity.Role;

public record UserOnboardingRequest(
        @NotNull
        Role role
) {
}
