//package kr.omong.todagtodag.domain.user.dto;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import java.time.LocalDate;
//
//public record SungjangOnboardingRequest(
//        @NotBlank
//        @Size(max = 5)
//        String growthName,
//
//        @NotBlank
//        String stickerBoardType,
//
//        @NotNull
//        @JsonFormat(pattern = "yyyy.MM.dd")
//        LocalDate birthday
//) {
//}
