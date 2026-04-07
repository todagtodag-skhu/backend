package kr.omong.todagtodag.domain.mission.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "mission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sticker_board_id", nullable = false)
    private StickerBoard stickerBoard;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String emoticon;

    @Column(nullable = false)
    private int rewardStickerCount;

    @Column(nullable = false)
    private int targetCount;

    @Column(nullable = false)
    @Builder.Default
    private boolean isCompleted = false;

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private MissionRequest missionRequest;

    public void update(String name, String emoticon, int rewardStickerCount, int targetCount) {
        this.name = name;
        this.emoticon = emoticon;
        this.rewardStickerCount = rewardStickerCount;
        this.targetCount = targetCount;
    }

    public void complete() {
        isCompleted = true;
    }

    public boolean hasRequest() {
        return this.missionRequest != null;
    }
}
