package kr.omong.todagtodag.domain.relation.exception;

import org.springframework.http.HttpStatus;

public enum RelationErrorCode {
    ROLE_MISMATCH(HttpStatus.FORBIDDEN, "해당 역할로 수행할 수 없는 작업입니다."),
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 초대 코드입니다."),
    RELATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 연결된 관계입니다."),
    RELATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 연결 관계입니다.");

    private final HttpStatus status;
    private final String message;

    RelationErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
