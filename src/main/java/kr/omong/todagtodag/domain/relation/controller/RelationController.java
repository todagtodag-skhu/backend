package kr.omong.todagtodag.domain.relation.controller;

import kr.omong.todagtodag.domain.relation.dto.UserRelationConnectRequest;
import kr.omong.todagtodag.domain.relation.dto.UserRelationInviteCodeResponse;
import kr.omong.todagtodag.domain.relation.service.RelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relation")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;

    @PostMapping("/invite-code/{sungjangId}")
    public ResponseEntity<UserRelationInviteCodeResponse> generateInviteCode(
            @PathVariable Long sungjangId
    ) {
        String code = relationService.generateInviteCode(sungjangId);
        return ResponseEntity.ok(new UserRelationInviteCodeResponse(code));
    }

    @PostMapping("/connect/{todakId}")
    public ResponseEntity<Void> connect(
            @PathVariable Long todakId,
            @RequestBody UserRelationConnectRequest request
    ) {
        relationService.connectByCode(todakId, request.code());
        return ResponseEntity.ok().build();
    }
}
