package kr.omong.todagtodag.domain.auth.dto;

import kr.omong.todagtodag.domain.user.entity.Role;

public record TestUserResponse(
        Long userId,
        String accessToken,
        Role role
) {
}
