package kr.omong.todagtodag.domain.auth.service;

import kr.omong.todagtodag.domain.auth.exception.AuthErrorCode;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.SungjangProfileRepository;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SungjangProfileRepository sungjangProfileRepository;
    private final UserRelationRepository userRelationRepository;

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        sungjangProfileRepository.deleteByUserId(userId);
        userRelationRepository.deleteAllByTodakIdOrSungjangId(userId, userId);
        userRepository.delete(user);
    }
}
