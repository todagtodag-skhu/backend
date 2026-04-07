package kr.omong.todagtodag.domain.sticker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@Entity
@Table(name = "pending_sticker")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PendingSticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sticker_board_id", nullable = false)
    private StickerBoard stickerBoard;

    @Column(nullable = false)
    private String missionName;

    @Column(nullable = false)
    private String emoticon;

    @Column(nullable = false)
    private LocalDate date;
}
