package kr.omong.todagtodag.domain.auth.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.auth.dto.SocialLoginRequest;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.auth.jwt.JwtTokenProvider;
import kr.omong.todagtodag.domain.auth.oauth.OAuthTokenVerifier;
import kr.omong.todagtodag.domain.auth.oauth.SocialProvider;
import kr.omong.todagtodag.domain.auth.oauth.VerifiedOAuthUser;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import kr.omong.todagtodag.domain.user.service.UserService;
import kr.omong.todagtodag.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SocialLoginService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<SocialProvider, OAuthTokenVerifier> verifierByProvider;

    public SocialLoginService(
            UserRepository userRepository,
            UserService userService,
            JwtTokenProvider jwtTokenProvider,
            List<OAuthTokenVerifier> verifiers
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.verifierByProvider = verifiers.stream()
                .collect(Collectors.toUnmodifiableMap(OAuthTokenVerifier::getProvider, Function.identity()));
    }

    @Transactional
    public AuthResponse login(SocialProvider provider, SocialLoginRequest request) {
        OAuthTokenVerifier verifier = verifierByProvider.get(provider);
        if (verifier == null) {
            throw new AuthException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
        }

        VerifiedOAuthUser verifiedUser = verifier.verify(request.token());
        return userRepository.findByProviderId(verifiedUser.providerId())
                .map(this::toExistingUserResponse)
                .orElseGet(() -> createNewUserResponse(verifiedUser.providerId()));
    }

    private AuthResponse toExistingUserResponse(User user) {
        return AuthResponse.builder()
                .isNewUser(false)
                .accessToken(jwtTokenProvider.createAccessToken(user))
                .role(user.getRole())
                .build();
    }

    private AuthResponse createNewUserResponse(String providerId) {
        User user = userService.createAppleUser(providerId);
        return AuthResponse.builder()
                .isNewUser(true)
                .accessToken(jwtTokenProvider.createAccessToken(user))
                .role(user.getRole())
                .build();
    }
}
