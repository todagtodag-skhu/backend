package kr.omong.todagtodag.domain.relation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RelationExceptionHandler {

    @ExceptionHandler(RelationException.class)
    public ResponseEntity<Map<String, String>> handleRelationException(RelationException exception) {
        return ResponseEntity.status(exception.getRelationErrorCode().getStatus())
                .body(Map.of(
                        "code", exception.getRelationErrorCode().name(),
                        "message", exception.getRelationErrorCode().getMessage()
                ));
    }
}
