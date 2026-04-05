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
import java.util.List;
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
        User todak = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        if (todak.getRole() == Role.SUNGJANG) {
            throw new AuthException(AuthErrorCode.WITHDRAW_FORBIDDEN_ROLE);
        }

        List<UserRelation> relations = userRelationRepository.findAllByTodakId(userId);
        if (relations.isEmpty()) {
            throw new RelationException(RelationErrorCode.RELATION_NOT_FOUND);
        }

        for (UserRelation relation : relations) {
            Long sungjangId = relation.getSungjang().getId();

            User sungjang = userRepository.findById(sungjangId)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

            userRelationRepository.delete(relation);
            sungjangProfileRepository.deleteByUserId(sungjangId);
            userRepository.delete(sungjang);
        }

        userRepository.delete(todak);
    }
}
