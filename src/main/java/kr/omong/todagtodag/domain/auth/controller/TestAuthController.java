package kr.omong.todagtodag.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.omong.todagtodag.domain.auth.dto.TestUserResponse;
import kr.omong.todagtodag.domain.auth.jwt.JwtTokenProvider;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RestController
@RequestMapping("/api/test")
@Tag(name = "테스트 인증 API", description = "local 프로필에서만 사용하는 테스트용 인증 API")
public class TestAuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public TestAuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(
            summary = "테스트 유저 생성 또는 조회",
            description = "local 프로필에서만 사용합니다. providerId로 PENDING 유저를 생성하거나 기존 유저를 재사용하고, userId와 accessToken을 반환합니다."
    )
    @PostMapping("/test-user")
    public ResponseEntity<TestUserResponse> createTestUser(
            @RequestParam(defaultValue = "test-provider-id") String providerId
    ) {
        User user = userService.getOrCreatePendingUser(providerId);
        return ResponseEntity.ok(new TestUserResponse(
                user.getId(),
                jwtTokenProvider.createAccessToken(user),
                user.getRole()
        ));
    }
}
