package kr.omong.todagtodag.domain.relation.dto;

public record UserRelationGetResponse(
        Long relationId,
        String childName
) {
}
