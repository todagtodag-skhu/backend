package kr.omong.todagtodag.domain.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode {
    APPLE_ID_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Apple 로그인 토큰 서명이 유효하지 않습니다."),
    APPLE_ID_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Apple 로그인 토큰이 만료되었습니다."),
    APPLE_ID_TOKEN_ISSUER_INVALID(HttpStatus.UNAUTHORIZED, "Apple 로그인 토큰의 issuer가 유효하지 않습니다."),
    APPLE_ID_TOKEN_AUDIENCE_INVALID(HttpStatus.UNAUTHORIZED, "Apple 로그인 토큰의 audience가 유효하지 않습니다."),
    APPLE_JWK_FETCH_FAILED(HttpStatus.BAD_GATEWAY, "Apple 공개 키(JWK)를 가져오는 데 실패했습니다."),
    APPLE_JWK_NOT_FOUND(HttpStatus.UNAUTHORIZED, "일치하는 Apple 공개 키(JWK)를 찾을 수 없습니다."),
    UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 방식입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_ONBOARDING_ROLE(HttpStatus.BAD_REQUEST, "온보딩 역할 값이 올바르지 않습니다."),
    ONBOARDING_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 온보딩이 완료된 사용자입니다."),
    INVALID_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "Authorization 헤더 형식이 올바르지 않습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Access Token이 유효하지 않습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다.");

    private final HttpStatus status;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
