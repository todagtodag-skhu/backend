package kr.omong.todagtodag.domain.relation.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRelationConnectRequest(
        @NotBlank
        String code
) {
}
