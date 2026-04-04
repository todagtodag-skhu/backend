package kr.omong.todagtodag.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth
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
    WITHDRAW_FORBIDDEN_ROLE(HttpStatus.FORBIDDEN, "성장이 유저는 회원 탈퇴를 요청할 수 없습니다."),
    INVALID_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "Authorization 헤더 형식이 올바르지 않습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Access Token이 유효하지 않습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다."),

    // Relation
    ROLE_MISMATCH(HttpStatus.FORBIDDEN, "해당 역할로 수행할 수 없는 작업입니다."),
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 초대 코드입니다."),
    RELATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 연결된 관계입니다."),
    RELATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 연결 관계입니다."),
    RELATION_TODAK_MISMATCH(HttpStatus.FORBIDDEN, "해당 관계의 토닥이가 아닙니다."),

    // Sticker
    STICKER_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 스티커판입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
