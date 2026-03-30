package kr.omong.todagtodag.user.dto;

import jakarta.validation.constraints.NotNull;
import kr.omong.todagtodag.user.entity.Role;

public record OnboardingRequest(
        @NotNull
        Role role
) {
}
