package kr.omong.todagtodag.auth.dto;

import kr.omong.todagtodag.user.entity.Role;

public record AuthResponse(
        boolean isNewUser,
        String accessToken,
        Role role
) {
}
