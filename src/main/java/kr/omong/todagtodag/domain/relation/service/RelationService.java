package kr.omong.todagtodag.domain.relation.service;

import jakarta.transaction.Transactional;
import kr.omong.todagtodag.domain.auth.exception.AuthErrorCode;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.model.InviteCodeGenerator;
import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.exception.RelationErrorCode;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import kr.omong.todagtodag.domain.relation.repository.InviteCodeRepository;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final UserRelationRepository userRelationRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final InviteCodeGenerator inviteCodeGenerator;
    private final UserRepository userRepository;

    public String generateInviteCode(Long sungjangId) {
        User sungjang = getUserById(sungjangId);
        validateRole(sungjang, Role.SUNGJANG);

        String code = inviteCodeGenerator.generate();
        inviteCodeRepository.save(code, sungjangId);
        return code;
    }

    @Transactional
    public void connectByCode(Long todakId, String code) {
        User todak = getUserById(todakId);
        validateRole(todak, Role.TODAK);

        Long sungjangId = inviteCodeRepository.findSungjangIdByCode(code)
                .orElseThrow(() -> new RelationException(RelationErrorCode.INVALID_INVITE_CODE));

        User sungjang = getUserById(sungjangId);

        if (userRelationRepository.existsByTodakIdAndSungjangId(todakId, sungjangId)) {
            throw new RelationException(RelationErrorCode.RELATION_ALREADY_EXISTS);
        }

        userRelationRepository.save(UserRelation.of(todak, sungjang));
        inviteCodeRepository.delete(code);
    }

    private void validateRole(User user, Role role) {
        if (!user.getRole().equals(role)) {
            throw new RelationException(RelationErrorCode.ROLE_MISMATCH);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }
}
