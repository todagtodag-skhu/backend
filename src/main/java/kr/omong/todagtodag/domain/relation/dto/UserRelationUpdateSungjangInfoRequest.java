package kr.omong.todagtodag.domain.relation.dto;

import java.util.Date;

public record UserRelationUpdateSungjangInfoRequest(
        String name,
        Date birthday
) {
}
