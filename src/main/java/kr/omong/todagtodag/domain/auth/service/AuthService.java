package kr.omong.todagtodag.domain.auth.service;

import java.time.Duration;
import java.time.LocalDateTime;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.auth.entity.RefreshToken;
import kr.omong.todagtodag.domain.auth.jwt.JwtTokenProvider;
import kr.omong.todagtodag.domain.auth.repository.RefreshTokenRepository;
import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import java.util.List;

import kr.omong.todagtodag.domain.auth.config.JwtProperties;
import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserRelationRepository userRelationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponse issueTokens(User user, boolean isNewUser) {
        LocalDateTime now = LocalDateTime.now();
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = refreshTokenGenerator.createRefreshToken();
        LocalDateTime expiryDate = now.plus(Duration.ofMillis(jwtProperties.refreshTokenExpirationMillSecond()));

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiryDate(expiryDate)
                .createdAt(now)
                .build());

        return AuthResponse.builder()
                .isNewUser(isNewUser)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole())
                .build();
    }

    @Transactional
    public AuthResponse refresh(String requestToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new AuthException(ErrorCode.REFRESH_TOKEN_INVALID));

        if (refreshToken.isExpired(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new AuthException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        refreshTokenRepository.delete(refreshToken);
        return issueTokens(refreshToken.getUser(), false);
    }

    @Transactional
    public void logout(Long userId, String requestToken) {
        User user = getAuthenticatedUser(userId);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new AuthException(ErrorCode.REFRESH_TOKEN_INVALID));

        if (!refreshToken.getUser().getId().equals(user.getId())) {
            throw new AuthException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        refreshTokenRepository.delete(refreshToken);
    }

    @Transactional
    public void withdraw(Long userId) {
        User todak = getAuthenticatedUser(userId);
        if (todak.getRole() == Role.SUNGJANG) {
            throw new AuthException(ErrorCode.WITHDRAW_FORBIDDEN_ROLE);
        }

        List<UserRelation> relations = userRelationRepository.findAllByTodakId(userId);
        if (relations.isEmpty()) {
            throw new RelationException(ErrorCode.RELATION_NOT_FOUND);
        }

        refreshTokenRepository.deleteAllByUser(todak);
        for (UserRelation relation : relations) {
            User sungjang = relation.getSungjang();
            refreshTokenRepository.deleteAllByUser(sungjang);
            userRelationRepository.delete(relation);
            userRepository.delete(sungjang);
        }

        userRepository.delete(todak);
    }

    private User getAuthenticatedUser(Long userId) {
        if (userId == null) {
            throw new AuthException(ErrorCode.ACCESS_TOKEN_INVALID);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
    }
}
