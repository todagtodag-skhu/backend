package kr.omong.todagtodag.domain.relation.exception;

import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class RelationException extends RuntimeException {

    private final ErrorCode errorCode;

    public RelationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public RelationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

}
