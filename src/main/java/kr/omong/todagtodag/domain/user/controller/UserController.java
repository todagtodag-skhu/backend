package kr.omong.todagtodag.domain.user.controller;

import jakarta.validation.Valid;
import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.auth.jwt.JwtTokenProvider;
import kr.omong.todagtodag.domain.user.dto.UserOnboardingRequest;
import kr.omong.todagtodag.domain.user.entity.User;
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
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/onboarding")
    public ResponseEntity<AuthResponse> onboarding(
            @AuthenticationPrincipal(expression = "principal") Long userId,
            @Valid @RequestBody UserOnboardingRequest request
    ) {
        User user = userService.onboard(userId, request.role());
        return ResponseEntity.ok(new AuthResponse(
                false,
                jwtTokenProvider.createAccessToken(user),
                user.getRole()
        ));
    }
}
