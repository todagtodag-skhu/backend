package kr.omong.todagtodag.domain.sticker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import kr.omong.todagtodag.domain.user.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = {
        "app.jwt.secret=01234567890123456789012345678901",
        "app.jwt.access-token-expiration-mill-second=86400000",
        "app.jwt.refresh-token-expiration-mill-second=1209600000",
        "app.oauth.apple.client-id=test-client-id"
})
@AutoConfigureMockMvc
@ActiveProfiles("local")
class StickerBoardFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUsersConnectByInviteCodeCreateAndGetStickerBoard() throws Exception {
        String suffix = UUID.randomUUID().toString();
        JsonNode sungjangUser = createTestUser("flow-sungjang-" + suffix);
        JsonNode todakUser = createTestUser("flow-todak-" + suffix);

        assertUnauthenticatedUserCannotAccessRelationApi();
        assertThat(sungjangUser.get("role").asText()).isEqualTo(Role.PENDING.name());
        assertThat(todakUser.get("role").asText()).isEqualTo(Role.PENDING.name());

        assertPendingUserCannotGenerateRelationInviteCode(sungjangUser.get("accessToken").asText());

        String inviteCode = generateOnboardingInviteCode(sungjangUser.get("accessToken").asText());
        assertThat(inviteCode).isNotBlank();

        JsonNode onboardTodak = onboardTodak(todakUser.get("accessToken").asText(), inviteCode);
        assertThat(onboardTodak.get("isNewUser").asBoolean()).isFalse();
        assertThat(onboardTodak.get("accessToken").asText()).isNotBlank();
        assertThat(onboardTodak.get("role").asText()).isEqualTo(Role.TODAK.name());

        String todakAccessToken = onboardTodak.get("accessToken").asText();
        JsonNode relations = getTodakRelations(todakAccessToken);
        assertThat(relations.get("relations")).hasSize(1);
        long relationId = relations.get("relations").get(0).get("relationId").asLong();

        long stickerBoardId = createStickerBoard(todakAccessToken, relationId);
        assertThat(stickerBoardId).isPositive();

        JsonNode stickerBoard = getTodakStickerBoard(todakAccessToken, relationId);
        assertThat(stickerBoard.get("stickerBoardId").asLong()).isEqualTo(stickerBoardId);
        assertThat(stickerBoard.get("name").asText()).isEqualTo("칭찬 스티커판");
        assertThat(stickerBoard.get("remainingStickerCount").asText()).isEqualTo("0/10");
        assertThat(stickerBoard.get("boardDesign").asText()).isEqualTo("TREE");
        assertThat(stickerBoard.get("finalReward").asText()).isEqualTo("동물원 가기");

        JsonNode missions = stickerBoard.get("missions");
        assertThat(missions).hasSize(2);
        assertThat(missions.get(0).get("name").asText()).isEqualTo("양치하기");
        assertThat(missions.get(0).get("days").get(0).asText()).isEqualTo("MON");
        assertThat(missions.get(0).get("days").get(1).asText()).isEqualTo("WED");
        assertThat(missions.get(0).get("dailyCount").asInt()).isEqualTo(1);
        assertThat(missions.get(0).get("rewardStickerCount").asInt()).isEqualTo(1);
        assertThat(missions.get(1).get("name").asText()).isEqualTo("책 읽기");
        assertThat(missions.get(1).get("days").get(0).asText()).isEqualTo("TUE");
        assertThat(missions.get(1).get("days").get(1).asText()).isEqualTo("THU");
        assertThat(missions.get(1).get("dailyCount").asInt()).isEqualTo(2);
        assertThat(missions.get(1).get("rewardStickerCount").asInt()).isEqualTo(2);
    }

    private JsonNode createTestUser(String providerId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/test/test-user")
                        .param("providerId", providerId))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private void assertUnauthenticatedUserCannotAccessRelationApi() throws Exception {
        mockMvc.perform(post("/relation/invite-code"))
                .andExpect(status().isForbidden());
    }

    private void assertPendingUserCannotGenerateRelationInviteCode(String accessToken) throws Exception {
        mockMvc.perform(post("/relation/invite-code")
                        .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
                .andExpect(status().isForbidden());
    }

    private String generateOnboardingInviteCode(String accessToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/users/onboarding/sungjang/invite-code")
                        .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("inviteCode")
                .asText();
    }

    private JsonNode onboardTodak(String accessToken, String inviteCode) throws Exception {
        MvcResult result = mockMvc.perform(post("/users/onboarding/todak")
                        .header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inviteCode": "%s"
                                }
                                """.formatted(inviteCode)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private JsonNode getTodakRelations(String accessToken) throws Exception {
        MvcResult result = mockMvc.perform(get("/relation/todak")
                        .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private long createStickerBoard(String accessToken, long relationId) throws Exception {
        MvcResult result = mockMvc.perform(post("/todak/sticker-board/{relationId}", relationId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "칭찬 스티커판",
                                  "stickerCount": "TEN",
                                  "boardDesign": "TREE",
                                  "missions": [
                                    {
                                      "name": "양치하기",
                                      "days": ["MON", "WED"],
                                      "dailyCount": 1,
                                      "rewardStickerCount": 1
                                    },
                                    {
                                      "name": "책 읽기",
                                      "days": ["TUE", "THU"],
                                      "dailyCount": 2,
                                      "rewardStickerCount": 2
                                    }
                                  ],
                                  "finalReward": "동물원 가기"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("stickerBoardId")
                .asLong();
    }

    private JsonNode getTodakStickerBoard(String accessToken, long relationId) throws Exception {
        MvcResult result = mockMvc.perform(get("/todak/sticker-board/{relationId}", relationId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String bearer(String accessToken) {
        return "Bearer " + accessToken;
    }
}
