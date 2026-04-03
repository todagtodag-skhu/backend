package kr.omong.todagtodag.domain.user.service;

import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.auth.exception.AuthErrorCode;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.auth.jwt.JwtTokenProvider;
import kr.omong.todagtodag.domain.relation.service.RelationService;
import kr.omong.todagtodag.domain.user.dto.SungjangOnboardingRequest;
import kr.omong.todagtodag.domain.user.dto.TodakOnboardingRequest;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.SungjangProfile;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.SungjangProfileRepository;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RelationService relationService;
    private final SungjangProfileRepository sungjangProfileRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User createAppleUser(String providerId) {
        return userRepository.save(
                User.builder()
                .providerId(providerId)
                .role(Role.PENDING)
                .build()
        );
    }

    @Transactional
    public User getOrCreatePendingUser(String providerId) {
        return userRepository.findByProviderId(providerId)
                .orElseGet(() -> createAppleUser(providerId));
    }

    @Transactional
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public AuthResponse onboardTodak(Long userId, TodakOnboardingRequest request) {
        User user = updateRoleForPendingUser(userId, Role.TODAK);
        relationService.connectByCode(userId, request.inviteCode());
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse onboardSungjang(Long userId, SungjangOnboardingRequest request) {
        User user = updateRoleForPendingUser(userId, Role.SUNGJANG);
        sungjangProfileRepository.save(
                SungjangProfile.builder()
                        .userId(userId)
                        .growthName(request.growthName())
                        .stickerBoardType(request.stickerBoardType())
                        .birthday(request.birthday())
                        .build()
        );
        return buildAuthResponse(user);
    }

    private User updateRoleForPendingUser(Long userId, Role role) {
        User user = getById(userId);
        if (!role.isSelectableForOnboarding()) {
            throw new AuthException(AuthErrorCode.INVALID_ONBOARDING_ROLE);
        }
        if (user.getRole() != Role.PENDING) {
            throw new AuthException(AuthErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }
        user.updateRole(role);
        return user;
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .isNewUser(false)
                .accessToken(jwtTokenProvider.createAccessToken(user))
                .role(user.getRole())
                .build();
    }
}
