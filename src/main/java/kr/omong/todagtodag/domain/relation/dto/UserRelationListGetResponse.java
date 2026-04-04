package kr.omong.todagtodag.domain.relation.dto;

import java.util.List;

public record UserRelationListGetResponse(
        List<UserRelationGetResponse> relations
) {
}
