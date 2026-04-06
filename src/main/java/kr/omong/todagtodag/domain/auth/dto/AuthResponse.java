package kr.omong.todagtodag.domain.auth.dto;

import kr.omong.todagtodag.domain.user.entity.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
        boolean isNewUser,
        String accessToken,
        String refreshToken,
        Role role
) {
}
