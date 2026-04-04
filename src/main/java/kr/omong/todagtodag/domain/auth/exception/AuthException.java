package kr.omong.todagtodag.domain.auth.exception;

import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
