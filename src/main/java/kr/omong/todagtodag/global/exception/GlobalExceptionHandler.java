package kr.omong.todagtodag.global.exception;

import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(RelationException.class)
    public ResponseEntity<Map<String, String>> handleRelationException(RelationException exception) {
        return ResponseEntity.status(exception.getErrorCode().getStatus())
                .body(Map.of(
                        "code", exception.getErrorCode().name(),
                        "message", exception.getErrorCode().getMessage()
                ));
    }
}
