package ibas.inchelin.web.controller;

import ibas.inchelin.domain.roulette.service.RouletteService;
import ibas.inchelin.web.dto.roulette.RouletteListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = RouletteController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class RouletteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RouletteService rouletteService;

    // ───────────────────────────────────────────────
    // GET /v1/roulette/options
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("룰렛 후보 조회 - 여러 가게 반환 성공")
    void getRouletteList_success() throws Exception {
        RouletteListResponse response = new RouletteListResponse(List.of(
                new RouletteListResponse.RouletteItemResponse(1L, "맛집1"),
                new RouletteListResponse.RouletteItemResponse(2L, "맛집2")
        ));

        given(rouletteService.getRouletteList()).willReturn(response);

        mockMvc.perform(get("/v1/roulette/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options.length()").value(2))
                .andExpect(jsonPath("$.options[0].id").value(1))
                .andExpect(jsonPath("$.options[0].name").value("맛집1"))
                .andExpect(jsonPath("$.options[1].id").value(2))
                .andExpect(jsonPath("$.options[1].name").value("맛집2"));

        verify(rouletteService).getRouletteList();
    }

    @Test
    @DisplayName("룰렛 후보 조회 - 가게가 없는 경우 빈 배열 반환")
    void getRouletteList_empty() throws Exception {
        given(rouletteService.getRouletteList()).willReturn(new RouletteListResponse(List.of()));

        mockMvc.perform(get("/v1/roulette/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options.length()").value(0));

        verify(rouletteService).getRouletteList();
    }

    @Test
    @DisplayName("룰렛 후보 조회 - 가게가 하나인 경우 정상 반환")
    void getRouletteList_singleItem() throws Exception {
        RouletteListResponse response = new RouletteListResponse(List.of(
                new RouletteListResponse.RouletteItemResponse(1L, "혼밥집")
        ));

        given(rouletteService.getRouletteList()).willReturn(response);

        mockMvc.perform(get("/v1/roulette/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.options.length()").value(1))
                .andExpect(jsonPath("$.options[0].id").value(1))
                .andExpect(jsonPath("$.options[0].name").value("혼밥집"));

        verify(rouletteService).getRouletteList();
    }
}
