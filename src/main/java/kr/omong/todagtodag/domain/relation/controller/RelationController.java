package kr.omong.todagtodag.domain.relation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.omong.todagtodag.domain.relation.dto.UserRelationConnectRequest;
import kr.omong.todagtodag.domain.relation.dto.UserRelationInviteCodeResponse;
import kr.omong.todagtodag.domain.relation.service.RelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relation")
@RequiredArgsConstructor
@Tag(name = "유저 관계 설정 API", description = "유저의 관계를 설정하기 위해 초대 코드 발급 및 인증 수행")
public class RelationController {

    private final RelationService relationService;

    @Operation(
            summary = "성장이 - 코드 생성",
            description =
                    """
                    성장이가 코드를 생성합니다.
                    
                    경로 변수에 성장이 유저 id가 필요하며, 생성된 코드를 body로 반환합니다.
                    
                    생성된 코드는 5분 동안 유지됩니다.
                    
                    토닥이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "코드 생성 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 성장이 유저로 요청"),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않음")
    })
    @PostMapping("/invite-code/sungjang")
    public ResponseEntity<UserRelationInviteCodeResponse> generateInviteCode(
            @AuthenticationPrincipal Long userId
    ) {
        String code = relationService.generateInviteCode(userId);
        return ResponseEntity.ok(new UserRelationInviteCodeResponse(code));
    }

    @Operation(
            summary = "토닥이 - 코드 입력",
            description =
                    """
                    토닥이 유저가 코드를 입력하여 성장이와 연결합니다.
                    
                    경로 변수에 토닥이 유저 id가 필요하고, code를 body로 입력해야 합니다.
                    
                    성장이 유저가 이 API를 실행할 경우, 그리고 이미 관계가 존재할 경우 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "코드 입력 성공, 관계 연결"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 코드"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청"),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않음"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 유저 관계")
    })
    @PostMapping("/connect/todak")
    public ResponseEntity<Void> connect(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserRelationConnectRequest request
    ) {
        relationService.connectByCode(userId, request.code());
        return ResponseEntity.ok().build();
    }
}
