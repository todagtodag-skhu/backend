package kr.omong.todagtodag.global.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import kr.omong.todagtodag.domain.sticker.exception.StickerBoardException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        if (isLocalDateFormatException(exception)) {
            return ResponseEntity.status(ErrorCode.INVALID_DATE_FORMAT.getStatus())
                    .body(Map.of(
                            "code", ErrorCode.INVALID_DATE_FORMAT.name(),
                            "message", ErrorCode.INVALID_DATE_FORMAT.getMessage()
                    ));
        }
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "code", "INVALID_REQUEST",
                        "message", "요청 본문을 읽을 수 없습니다."
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

    @ExceptionHandler(StickerBoardException.class)
    public ResponseEntity<Map<String, String>> handleStickerBoardException(StickerBoardException exception) {
        return ResponseEntity.status(exception.getErrorCode().getStatus())
                .body(Map.of(
                        "code", exception.getErrorCode().name(),
                        "message", exception.getErrorCode().getMessage()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body("잘못된 요청 값입니다.");
    }

    private boolean isLocalDateFormatException(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof InvalidFormatException invalidFormatException
                    && invalidFormatException.getTargetType() == LocalDate.class) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
