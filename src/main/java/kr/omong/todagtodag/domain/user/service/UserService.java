package kr.omong.todagtodag.domain.user.service;

import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.relation.dto.UserRelationConnectResponse;
import kr.omong.todagtodag.domain.relation.service.RelationService;
import kr.omong.todagtodag.domain.user.dto.TodakOnboardingRequest;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RelationService relationService;

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
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public AuthResponse onboardTodak(Long userId, TodakOnboardingRequest request) {
        UserRelationConnectResponse relation = relationService.connectByCode(userId, request.inviteCode());
        return AuthResponse.builder()
                .isNewUser(false)
                .accessToken(relation.accessToken())
                .role(relation.role())
                .build();
    }
}
