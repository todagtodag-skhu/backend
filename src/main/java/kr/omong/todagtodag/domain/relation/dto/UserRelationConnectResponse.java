package kr.omong.todagtodag.domain.relation.dto;

import kr.omong.todagtodag.domain.user.entity.Role;

public record UserRelationConnectResponse(
        Long relationId,
        String accessToken,
        Role role
) {
}
