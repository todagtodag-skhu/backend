package kr.omong.todagtodag.auth.controller;

import java.util.Map;
import kr.omong.todagtodag.auth.exception.AuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuthException(AuthException exception) {
        return ResponseEntity.status(exception.getErrorCode().getStatus())
                .body(Map.of(
                        "code", exception.getErrorCode().name(),
                        "message", exception.getErrorCode().getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "code", "INVALID_REQUEST",
                        "message", "요청 값 검증에 실패했습니다."
                ));
    }
}
