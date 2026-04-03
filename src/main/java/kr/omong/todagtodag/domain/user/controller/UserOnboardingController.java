package kr.omong.todagtodag.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.user.dto.SungjangOnboardingRequest;
import kr.omong.todagtodag.domain.user.dto.TodakOnboardingRequest;
import kr.omong.todagtodag.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "유저 온보딩, 로그아웃, 회원 탈퇴를 처리합니다.")
public class UserOnboardingController {

    private final UserService userService;

    @Operation(
            summary = "토닥이 온보딩",
            description = "PENDING 권한의 access token으로 호출합니다. inviteCode를 입력하면 역할을 TODAK으로 변경하고 관계를 연결합니다."
    )
    @PostMapping("/onboarding/todak")
    public ResponseEntity<AuthResponse> onboardTodak(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody TodakOnboardingRequest request
    ) {
        return ResponseEntity.ok(userService.onboardTodak(userId, request));
    }

    @Operation(
            summary = "성장이 온보딩",
            description = "PENDING 권한의 access token으로 호출합니다. 성장이 이름, 스티커판 종류, 생일을 저장하고 역할을 SUNGJANG으로 변경합니다."
    )
    @PostMapping("/onboarding/sungjang")
    public ResponseEntity<AuthResponse> onboardSungjang(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody SungjangOnboardingRequest request
    ) {
        return ResponseEntity.ok(userService.onboardSungjang(userId, request));
    }

    @Operation(
            summary = "로그아웃",
            description = "클라이언트에서 accessToken을 삭제하여 로그아웃 처리합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인된 사용자의 계정을 삭제합니다."
    )
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal Long userId
    ) {
        userService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }
}
