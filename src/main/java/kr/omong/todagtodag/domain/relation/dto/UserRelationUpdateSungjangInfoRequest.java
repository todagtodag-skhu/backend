package kr.omong.todagtodag.domain.relation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record UserRelationUpdateSungjangInfoRequest(
        String sungjangName,
        @JsonFormat(pattern = "yyyy.MM.dd")
        LocalDate sungjangBirthday
) {
}
