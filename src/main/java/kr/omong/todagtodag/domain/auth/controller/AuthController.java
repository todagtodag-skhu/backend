package kr.omong.todagtodag.domain.auth.controller;

import jakarta.validation.Valid;
import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.auth.dto.SocialLoginRequest;
import kr.omong.todagtodag.domain.auth.oauth.SocialProvider;
import kr.omong.todagtodag.domain.auth.service.SocialLoginService;
import kr.omong.todagtodag.global.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final SocialLoginService socialLoginService;

    @Operation(summary = "소셜 로그인", description = "소셜 SDK에서 받은 토큰으로 JWT(access/refresh)를 발급합니다.")
    @PostMapping("/auth/login/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AuthResponse> socialLogin(
            @PathVariable SocialProvider provider,
            @RequestBody @Valid SocialLoginRequest request
    ) {
        AuthResponse response = socialLoginService.login(provider, request);
        return ApiResponse.success(response);
    }
}
