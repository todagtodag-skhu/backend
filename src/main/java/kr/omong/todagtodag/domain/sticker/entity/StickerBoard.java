package kr.omong.todagtodag.domain.sticker.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.omong.todagtodag.domain.mission.entity.Mission;
import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@Table(name = "sticker_board")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StickerBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_relation_id", nullable = false)
    private UserRelation userRelation;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private StickerCount stickerCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private BoardDesign boardDesign;

    @Column(nullable = false)
    private String finalReward;

    @Column(nullable = false)
    @Builder.Default
    private boolean isCompleted = false;

    @Builder.Default
    @OneToMany(mappedBy = "stickerBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "stickerBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sticker> stickers = new ArrayList<>();

    public void update(String name, StickerCount stickerCount, BoardDesign boardDesign, String finalReward) {
        this.name = name;
        this.stickerCount = stickerCount;
        this.boardDesign = boardDesign;
        this.finalReward = finalReward;
    }
}
