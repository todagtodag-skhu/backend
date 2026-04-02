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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "유저 온보딩 API", description = "신규 유저의 역할별 온보딩을 처리합니다.")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "토닥이 온보딩",
            description = "임시 수동 테스트용으로 userId를 쿼리 파라미터로 받습니다. inviteCode를 입력하면 역할을 TODAK으로 변경하고 관계를 연결합니다."
    )
    @PostMapping("/onboarding/todak")
    public ResponseEntity<AuthResponse> onboardTodak(
            @RequestParam Long userId,
            @Valid @RequestBody TodakOnboardingRequest request
    ) {
        return ResponseEntity.ok(userService.onboardTodak(userId, request));
    }

    @Operation(
            summary = "성장이 온보딩",
            description = "임시 수동 테스트용으로 userId를 쿼리 파라미터로 받습니다. 성장이 이름, 스티커판 종류, 생일을 저장하고 역할을 SUNGJANG으로 변경합니다."
    )
    @PostMapping("/onboarding/sungjang")
    public ResponseEntity<AuthResponse> onboardSungjang(
            @RequestParam Long userId,
            @Valid @RequestBody SungjangOnboardingRequest request
    ) {
        return ResponseEntity.ok(userService.onboardSungjang(userId, request));
    }
}
