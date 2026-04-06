package kr.omong.todagtodag.domain.relation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record UserRelationUpdateChildInfoRequest(
        String childName,
        @JsonFormat(pattern = "yyyy.MM.dd")
        LocalDate childBirthday
) {
}
