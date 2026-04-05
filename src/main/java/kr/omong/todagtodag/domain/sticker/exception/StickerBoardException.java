package kr.omong.todagtodag.domain.sticker.exception;

import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class StickerBoardException extends RuntimeException {

    private final ErrorCode errorCode;

    public StickerBoardException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public StickerBoardException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
