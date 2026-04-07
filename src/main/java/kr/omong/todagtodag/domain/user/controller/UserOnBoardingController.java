package kr.omong.todagtodag.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.relation.dto.UserRelationInviteCodeResponse;
import kr.omong.todagtodag.domain.user.dto.TodakOnboardingRequest;
import kr.omong.todagtodag.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "유저 온보딩 API", description = "신규 유저의 역할별 온보딩을 처리합니다.")
public class UserOnBoardingController {

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
            summary = "성장이 온보딩 - 초대코드 생성",
            description = "PENDING 권한의 access token으로 호출합니다. 토닥이와 최초 연결하기 위한 초대코드를 생성합니다."
    )
    @PostMapping("/onboarding/sungjang/invite-code")
    public ResponseEntity<UserRelationInviteCodeResponse> generateSungjangOnboardingInviteCode(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(userService.generateSungjangOnboardingInviteCode(userId));
    }
}
