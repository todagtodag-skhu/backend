package kr.omong.todagtodag.domain.sticker.entity;

import lombok.Getter;

@Getter
public enum StickerCount {
    TWENTY(20),
    THIRTY(30),
    FIFTY(50);

    private final int value;

    StickerCount(int value) {
        this.value = value;
    }

}
