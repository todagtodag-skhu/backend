package kr.omong.todagtodag.domain.relation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserRelationUpdateSungjangInfoRequest(
        @NotBlank
        String sungjangName,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate sungjangBirthday
) {
}
