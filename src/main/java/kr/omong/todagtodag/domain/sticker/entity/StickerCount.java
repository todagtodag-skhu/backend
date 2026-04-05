package kr.omong.todagtodag.domain.sticker.entity;

import lombok.Getter;

@Getter
public enum StickerCount {
    TEN(10),
    TWENTY(20),
    THIRTY(30);

    private final int value;

    StickerCount(int value) {
        this.value = value;
    }

}
