package kr.omong.todagtodag.domain.relation.service;

import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.relation.dto.UserRelationGetResponse;
import kr.omong.todagtodag.domain.relation.dto.UserRelationInviteCodeValidateResponse;
import kr.omong.todagtodag.domain.relation.dto.UserRelationListGetResponse;
import kr.omong.todagtodag.domain.relation.model.InviteCodeGenerator;
import kr.omong.todagtodag.domain.relation.dto.UserRelationUpdateChildInfoRequest;
import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final UserRelationRepository userRelationRepository;
    private final InviteCodeGenerator inviteCodeGenerator;
    private final UserRepository userRepository;

    public String generateInviteCode(Long sungjangId) {
        User sungjang = getUserById(sungjangId);
        validateInviteCodeIssuer(sungjang);

        String code = inviteCodeGenerator.generate();
        sungjang.updateInviteCode(code);
        return code;
    }

    @Transactional(readOnly = true)
    public UserRelationInviteCodeValidateResponse validateInviteCode(String code) {
        User sungjang = userRepository.findByInviteCode(code)
                .orElseThrow(() -> new RelationException(ErrorCode.INVALID_INVITE_CODE));
        return new UserRelationInviteCodeValidateResponse(sungjang.getId());
    }

    @Transactional
    public Long connectByCode(Long todakId, String code) {
        return connectByCode(todakId, code, null, null);
    }

    @Transactional
    public Long connectByCode(Long todakId, String code, String childName, LocalDate childBirthday) {
        User todak = resolveTodakUser(todakId);

        User sungjang = userRepository.findByInviteCode(code)
                .orElseThrow(() -> new RelationException(ErrorCode.INVALID_INVITE_CODE));
        promoteToSungjangIfPending(sungjang);
        Long sungjangId = sungjang.getId();

        if (userRelationRepository.existsByTodakIdAndSungjangId(todakId, sungjangId)) {
            throw new RelationException(ErrorCode.RELATION_ALREADY_EXISTS);
        }

        UserRelation relation = userRelationRepository.save(
                UserRelation.of(todak, sungjang, childName, childBirthday)
        );

        return relation.getId();
    }

    public void updateChildInfoByRelationId(Long todakId, Long relationId, UserRelationUpdateChildInfoRequest request) {
        User todak = getUserById(todakId);
        UserRelation relation = getRelationById(relationId);

        validateRole(todak, Role.TODAK);
        validateRelation(todak, relation);

        relation.updateChildInfo(
                request.childName(),
                request.childBirthday()
        );
    }

    @Transactional(readOnly = true)
    public UserRelationListGetResponse getRelations(Long todakId) {
        User todak = getUserById(todakId);
        validateRole(todak, Role.TODAK);

        List<UserRelationGetResponse> relations = userRelationRepository.findAllByTodak(todak)
                .stream()
                .map(r -> new UserRelationGetResponse(r.getId(), r.getChildName()))
                .toList();

        return new UserRelationListGetResponse(relations);
    }

    private void validateInviteCodeIssuer(User user) {
        if (user.getRole() != Role.PENDING && user.getRole() != Role.SUNGJANG) {
            throw new RelationException(ErrorCode.ROLE_MISMATCH);
        }
    }

    private void validateRole(User user, Role role) {
        if (!user.getRole().equals(role)) {
            throw new RelationException(ErrorCode.ROLE_MISMATCH);
        }
    }

    private User resolveTodakUser(Long userId) {
        User user = getUserById(userId);
        if (user.getRole() == Role.PENDING) {
            user.updateRole(Role.TODAK);
            return user;
        }
        validateRole(user, Role.TODAK);
        return user;
    }

    private void promoteToSungjangIfPending(User user) {
        if (user.getRole() == Role.PENDING) {
            user.updateRole(Role.SUNGJANG);
            return;
        }
        validateRole(user, Role.SUNGJANG);
    }

    public void validateRelation(User todak, UserRelation relation) {
        if (!relation.isOwnedByTodak(todak)) {
            throw new RelationException(ErrorCode.RELATION_TODAK_MISMATCH);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
    }

    private UserRelation getRelationById(Long relationId) {
        return userRelationRepository.findById(relationId)
                .orElseThrow(() -> new RelationException(ErrorCode.RELATION_NOT_FOUND));
    }
}
