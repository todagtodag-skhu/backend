package kr.omong.todagtodag.domain.user.service;

import kr.omong.todagtodag.domain.auth.exception.AuthErrorCode;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User createAppleUser(String providerId) {
        return userRepository.save(User.builder()
                .providerId(providerId)
                .role(Role.TODAK)
                .build());
    }

    @Transactional
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public User onboard(Long userId, Role role) {
        User user = getById(userId);
        user.updateRole(role);
        return user;
    }
}
