package kr.omong.todagtodag.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.auth.dto.SocialLoginRequest;
import kr.omong.todagtodag.domain.auth.oauth.SocialProvider;
import kr.omong.todagtodag.domain.auth.service.AuthService;
import kr.omong.todagtodag.domain.auth.service.SocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인, 로그아웃, 회원 탈퇴를 처리합니다.")
public class AuthController {

    private final AuthService authService;
    private final SocialLoginService socialLoginService;

    @Operation(summary = "소셜 로그인", description = "소셜 SDK에서 받은 토큰으로 JWT(access/refresh)를 발급합니다.")
    @PostMapping("/api/auth/login/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthResponse> socialLogin(
            @PathVariable SocialProvider provider,
            @RequestBody @Valid SocialLoginRequest request
    ) {
        AuthResponse response = socialLoginService.login(provider, request);
        return ResponseEntity.ok(response);

    }

    //앞으로 refresh Token 도입 시 사용할 예정입니다.
    @Operation(
            summary = "로그아웃",
            description = "클라이언트에서 accessToken을 삭제하여 로그아웃 처리합니다."
    )
    @PostMapping("/users/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인된 사용자의 계정을 삭제합니다."
    )
    @DeleteMapping("/users/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal Long userId
    ) {
        authService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }
}
