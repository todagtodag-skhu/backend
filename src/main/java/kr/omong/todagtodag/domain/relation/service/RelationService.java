package kr.omong.todagtodag.domain.relation.service;

import jakarta.transaction.Transactional;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.relation.model.InviteCodeGenerator;
import kr.omong.todagtodag.domain.relation.dto.UserRelationUpdateSungjangInfoRequest;
import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import kr.omong.todagtodag.domain.relation.repository.InviteCodeRepository;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import kr.omong.todagtodag.global.exception.ErrorCode;
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
    public Long connectByCode(Long todakId, String code) {
        User todak = getUserById(todakId);
        validateRole(todak, Role.TODAK);

        Long sungjangId = inviteCodeRepository.findSungjangIdByCode(code)
                .orElseThrow(() -> new RelationException(ErrorCode.INVALID_INVITE_CODE));

        User sungjang = getUserById(sungjangId);

        if (userRelationRepository.existsByTodakIdAndSungjangId(todakId, sungjangId)) {
            throw new RelationException(ErrorCode.RELATION_ALREADY_EXISTS);
        }

        inviteCodeRepository.delete(code);
        UserRelation relation = userRelationRepository.save(UserRelation.of(todak, sungjang));

        return relation.getId();
    }

    public void updateSungjangInfoByRelationId(Long todakId, Long relationId, UserRelationUpdateSungjangInfoRequest request) {
        User todak = getUserById(todakId);
        UserRelation relation = getRelationById(relationId);

        validateRole(todak, Role.TODAK);
        validateRelation(todak, relation);

        relation.updateSungjangInfo(
                request.name(),
                request.birthday()
        );
    }

    private void validateRole(User user, Role role) {
        if (!user.getRole().equals(role)) {
            throw new RelationException(ErrorCode.ROLE_MISMATCH);
        }
    }

    private void validateRelation(User todak, UserRelation relation) {
        if (relation.getTodak() != todak) {
            throw new RelationException(RelationErrorCode.RELATION_TODAK_MISMATCH);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
    }

    private UserRelation getRelationById(Long relationId) {
        return userRelationRepository.findById(relationId)
                .orElseThrow(() -> new RelationException(RelationErrorCode.RELATION_NOT_FOUND));
    }
}
