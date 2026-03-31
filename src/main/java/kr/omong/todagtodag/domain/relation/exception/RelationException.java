package kr.omong.todagtodag.domain.relation.exception;

public class RelationException extends RuntimeException {

    private final RelationErrorCode relationErrorCode;

    public RelationException(RelationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.relationErrorCode = errorCode;
    }

    public RelationException(RelationErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.relationErrorCode = errorCode;
    }

    public RelationErrorCode getRelationErrorCode() {
        return relationErrorCode;
    }
}
