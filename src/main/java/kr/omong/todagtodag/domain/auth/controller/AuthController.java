package kr.omong.todagtodag.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.auth.dto.RefreshRequest;
import kr.omong.todagtodag.domain.auth.dto.SocialLoginRequest;
import kr.omong.todagtodag.domain.auth.oauth.SocialProvider;
import kr.omong.todagtodag.domain.auth.service.AuthService;
import kr.omong.todagtodag.domain.auth.service.SocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인, 로그아웃, 회원 탈퇴를 처리합니다.")
public class  AuthController {

    private final AuthService authService;
    private final SocialLoginService socialLoginService;

    @Operation(
            summary = "소셜 로그인",
            description =
                    """
                    소셜 SDK에서 받은 토큰으로 로그인합니다.
                    
                    provider 경로 값에는 로그인할 소셜 플랫폼을 넣어야 하며, body에는 SDK에서 받은 token 값을 담아 요청합니다.
                    
                    로그인에 성공하면 accessToken, refreshToken, role, 신규 유저 여부를 반환합니다.
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
            summary = "토큰 재발급",
            description =
                    """
                    Refresh Token으로 Access Token과 Refresh Token을 재발급합니다.
                    
                    body에 refreshToken을 담아 호출해야 하며, 재발급에 성공하면 새로운 accessToken과 refreshToken을 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않거나 만료됨")
    })
    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody @Valid RefreshRequest request
    ) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @Operation(
            summary = "로그아웃",
            description =
                    """
                    로그아웃을 처리합니다.
                    
                    헤더에 accessToken을 담아 호출해야 하며, body에는 현재 기기에서 사용 중인 refreshToken을 담아 요청해야 합니다.
                    
                    서버에는 현재 기기의 Refresh Token만 삭제하며, 다른 기기에서 로그인한 Refresh Token은 유지됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 응답 성공"),
            @ApiResponse(responseCode = "401", description = "토큰이 없거나 유효하지 않음")
    })
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid RefreshRequest request
    ) {
        authService.logout(userId, request.refreshToken());
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
