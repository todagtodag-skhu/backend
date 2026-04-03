package kr.omong.todagtodag.domain.auth.service;

import kr.omong.todagtodag.domain.auth.exception.AuthErrorCode;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.exception.RelationErrorCode;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
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
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        if (requester.getRole() == Role.SUNGJANG) {
            throw new AuthException(AuthErrorCode.WITHDRAW_FORBIDDEN_ROLE);
        }

        UserRelation relation = userRelationRepository.findByTodakId(userId)
                .orElseThrow(() -> new RelationException(RelationErrorCode.RELATION_NOT_FOUND));

        Long todakId = relation.getTodak().getId();
        Long sungjangId = relation.getSungjang().getId();

        User todak = userRepository.findById(todakId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        User sungjang = userRepository.findById(sungjangId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        userRelationRepository.delete(relation);
        sungjangProfileRepository.deleteByUserId(sungjangId);
        userRepository.delete(todak);
        userRepository.delete(sungjang);
    }
}
