package kr.omong.todagtodag.domain.sticker.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardTodakGetResponse;
import kr.omong.todagtodag.domain.sticker.entity.BoardDesign;
import kr.omong.todagtodag.domain.mission.entity.Mission;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import kr.omong.todagtodag.domain.sticker.entity.StickerCount;
import kr.omong.todagtodag.domain.mission.repository.MissionRepository;
import kr.omong.todagtodag.domain.sticker.repository.StickerBoardRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StickerBoardServiceTest {

    @Autowired
    private StickerBoardService stickerBoardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRelationRepository userRelationRepository;

    @Autowired
    private StickerBoardRepository stickerBoardRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Test
    void getStickerBoardByRelationIdReturnsJsonSerializableResponse() throws Exception {
        User todak = userRepository.save(User.builder()
                .providerId("todak-sticker-board-test")
                .role(Role.TODAK)
                .build());
        User sungjang = userRepository.save(User.builder()
                .providerId("sungjang-sticker-board-test")
                .role(Role.SUNGJANG)
                .build());
        UserRelation relation = userRelationRepository.save(UserRelation.of(todak, sungjang));
        StickerBoard stickerBoard = stickerBoardRepository.save(StickerBoard.builder()
                .userRelation(relation)
                .name("첫 스티커판")
                .stickerCount(StickerCount.TWENTY)
                .boardDesign(BoardDesign.TIGER)
                .finalReward("놀이공원")
                .build());
        missionRepository.save(Mission.builder()
                .stickerBoard(stickerBoard)
                .name("양치하기")
                .emoticon("TOOTH")
                .rewardStickerCount(1)
                .targetCount(2)
                .build());

        StickerBoardTodakGetResponse response = stickerBoardService.getStickerBoardByRelationId(
                todak.getId(),
                relation.getId()
        );

        String json = objectMapper.writeValueAsString(response);

        assertThat(json)
                .contains("\"emoticon\":\"TOOTH\"")
                .contains("\"targetCount\":2")
                .contains("\"isRequested\":false");
    }
}
