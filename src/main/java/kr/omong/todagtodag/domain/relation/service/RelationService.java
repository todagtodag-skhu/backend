package kr.omong.todagtodag.domain.relation.service;

import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.auth.dto.AuthResponse;
import kr.omong.todagtodag.domain.auth.service.AuthService;
import kr.omong.todagtodag.domain.relation.dto.UserRelationConnectResponse;
import kr.omong.todagtodag.domain.relation.dto.UserRelationGetResponse;
import kr.omong.todagtodag.domain.relation.dto.UserRelationListGetResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final UserRelationRepository userRelationRepository;
    private final InviteCodeGenerator inviteCodeGenerator;
    private final InviteCodeRepository inviteCodeRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public String generateInviteCode(Long sungjangId) {
        User sungjang = getUserById(sungjangId);
        validateRole(sungjang, Role.SUNGJANG);

        return saveInviteCode(sungjang);
    }

    public String generateOnboardingInviteCode(Long pendingSungjangId) {
        User pendingSungjang = getUserById(pendingSungjangId);
        validateRole(pendingSungjang, Role.PENDING);

        return saveInviteCode(pendingSungjang);
    }

    @Transactional
    public UserRelationConnectResponse connectByCode(Long todakId, String code) {
        User todak = getUserById(todakId);
        validateRole(todak, Role.TODAK);
        return createRelationByCode(todak, code);
    }

    @Transactional
    public UserRelationConnectResponse onboardTodakByCode(Long todakId, String code) {
        User todak = resolvePendingTodakUser(todakId);
        return createRelationByCode(todak, code);
    }

    private UserRelationConnectResponse createRelationByCode(User todak, String code) {
        Long sungjangId = inviteCodeRepository.findSungjangIdByCode(code)
                .orElseThrow(() -> new RelationException(ErrorCode.INVALID_INVITE_CODE));
        User sungjang = getUserById(sungjangId);
        promoteToSungjangIfPending(sungjang);

        if (userRelationRepository.existsByTodakIdAndSungjangId(todak.getId(), sungjangId)) {
            throw new RelationException(ErrorCode.RELATION_ALREADY_EXISTS);
        }

        inviteCodeRepository.delete(code);
        UserRelation relation = userRelationRepository.save(
                UserRelation.of(todak, sungjang)
        );
        AuthResponse authResponse = authService.issueTokens(todak, false);

        return new UserRelationConnectResponse(
                relation.getId(),
                authResponse.accessToken(),
                authResponse.refreshToken(),
                authResponse.role()
        );
    }

    @Transactional
    public void updateSungjangInfoByRelationId(Long todakId, Long relationId, UserRelationUpdateSungjangInfoRequest request) {
        User todak = getUserById(todakId);
        UserRelation relation = getRelationById(relationId);

        validateRole(todak, Role.TODAK);
        validateRelation(todak, relation);

        relation.updateSungjangInfo(
                request.sungjangName(),
                request.sungjangBirthday()
        );
    }

    @Transactional(readOnly = true)
    public UserRelationListGetResponse getRelations(Long todakId) {
        User todak = getUserById(todakId);
        validateRole(todak, Role.TODAK);

        List<UserRelationGetResponse> relations = userRelationRepository.findAllByTodak(todak)
                .stream()
                .map(r -> new UserRelationGetResponse(r.getId(), r.getSungjangName()))
                .toList();

        return new UserRelationListGetResponse(relations);
    }

    private String saveInviteCode(User sungjang) {
        String code = inviteCodeGenerator.generate();
        inviteCodeRepository.save(code, sungjang.getId());
        return code;
    }

    public void validateRole(User user, Role role) {
        if (!user.getRole().equals(role)) {
            throw new RelationException(ErrorCode.ROLE_MISMATCH);
        }
    }

    private User resolvePendingTodakUser(Long userId) {
        User user = getUserById(userId);
        if (user.getRole() != Role.PENDING) {
            throw new AuthException(ErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }
        user.updateRole(Role.TODAK);
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

    public UserRelation getRelationById(Long relationId) {
        return userRelationRepository.findById(relationId)
                .orElseThrow(() -> new RelationException(ErrorCode.RELATION_NOT_FOUND));
    }

    public UserRelation findRelationBySungjang(User sungjang) {
        return userRelationRepository.findBySungjang(sungjang)
                .orElseThrow(() -> new RelationException(ErrorCode.RELATION_SUNGJANG_NOT_FOUND));
    }
}
