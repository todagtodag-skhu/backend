package kr.omong.todagtodag.domain.auth.dto;

import kr.omong.todagtodag.domain.user.entity.Role;

public record AuthResponse(
        boolean isNewUser,
        String accessToken,
        Role role
) {
}
