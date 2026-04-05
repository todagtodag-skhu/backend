package kr.omong.todagtodag.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "소셜 로그인",
            description =
                    """
                    소셜 SDK에서 받은 토큰으로 로그인합니다.
                    
                    provider 경로 값에는 로그인할 소셜 플랫폼을 넣어야 하며, body에는 SDK에서 받은 token 값을 담아 요청합니다.
                    
                    로그인에 성공하면 accessToken, role, 신규 유저 여부를 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 소셜 로그인 방식"),
            @ApiResponse(responseCode = "401", description = "소셜 토큰이 유효하지 않거나 만료됨"),
            @ApiResponse(responseCode = "502", description = "Apple 공개 키 조회 실패")
    })
    @PostMapping("/api/auth/login/{provider}")
    public ResponseEntity<AuthResponse> socialLogin(
            @PathVariable SocialProvider provider,
            @RequestBody @Valid SocialLoginRequest request
    ) {
        AuthResponse response = socialLoginService.login(provider, request);
        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "로그아웃 (Refresh Token 도입 예정)",
            description =
                    """
                    로그아웃을 처리합니다.
                    
                    현재는 JWT Access Token만 사용하고 있으므로, 헤더에 accessToken을 담아 호출한 뒤 클라이언트에서 토큰을 삭제하는 방식으로 로그아웃을 처리합니다.
                    
                    추후 Refresh Token이 도입되면, 해당 API에서 Refresh Token 무효화 처리까지 함께 수행할 예정입니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 응답 성공"),
            @ApiResponse(responseCode = "401", description = "토큰이 없거나 유효하지 않음")
    })
    @PostMapping("/users/logout")
    // 현재는 Access Token만 사용하므로 서버 측 로그아웃 처리는 없다.
    // 추후 Refresh Token을 도입하면 이 API에서 토큰 무효화 로직을 처리할 예정이다.
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "회원 탈퇴",
            description =
                    """
                    회원 탈퇴를 처리합니다.
                    
                    헤더에 accessToken을 담아 호출해야 합니다.
                    
                    현재 로그인한 토닥이 유저의 userId로 모든 관계를 조회한 뒤, 연결된 모든 성장이와 토닥이 계정을 함께 삭제합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "토큰이 없거나 유효하지 않음"),
            @ApiResponse(responseCode = "403", description = "성장이 유저는 회원 탈퇴 요청 불가"),
            @ApiResponse(responseCode = "404", description = "유저 또는 관계를 찾을 수 없음")
    })
    @DeleteMapping("/users/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal Long userId
    ) {
        authService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }
}
