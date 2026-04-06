package kr.omong.todagtodag.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TodakOnboardingRequest(
        @NotBlank
        String inviteCode,

        @NotBlank
        String childName,

        @NotNull
        @JsonFormat(pattern = "yyyy.MM.dd")
        LocalDate childBirthday
) {
}
