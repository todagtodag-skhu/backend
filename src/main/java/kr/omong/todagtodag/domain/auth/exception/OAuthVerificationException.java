package kr.omong.todagtodag.domain.auth.exception;

import kr.omong.todagtodag.global.exception.ErrorCode;

public class OAuthVerificationException extends AuthException {

    public OAuthVerificationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OAuthVerificationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
