package kr.omong.todagtodag.domain.auth.exception;

public class OAuthVerificationException extends AuthException {

    public OAuthVerificationException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public OAuthVerificationException(AuthErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
