package ibas.inchelin.web.controller;

import ibas.inchelin.domain.store.service.StoreService;
import ibas.inchelin.web.dto.store.StoreInfoResponse;
import ibas.inchelin.web.dto.store.StoreListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import jakarta.servlet.ServletException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = StoreController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class StoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    StoreService storeService;

    // ───────────────────────────────────────────────
    // GET /v1/shops
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("가게 목록 조회 - 카테고리 없이 전체 조회 성공")
    void getStoreList_noCategory() throws Exception {
        StoreListResponse.StoreListItemResponse item = new StoreListResponse.StoreListItemResponse(
                1L, "맛집1", "한식", "https://cdn.example.com/thumb1.jpg", 4.5, 10L, 4.3);
        StoreListResponse response = new StoreListResponse(List.of(item));

        given(storeService.getStoreList(null)).willReturn(response);

        mockMvc.perform(get("/v1/shops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stores").isArray())
                .andExpect(jsonPath("$.stores.length()").value(1))
                .andExpect(jsonPath("$.stores[0].id").value(1))
                .andExpect(jsonPath("$.stores[0].name").value("맛집1"))
                .andExpect(jsonPath("$.stores[0].category").value("한식"))
                .andExpect(jsonPath("$.stores[0].averageRating").value(4.5))
                .andExpect(jsonPath("$.stores[0].reviewCount").value(10));

        verify(storeService).getStoreList(null);
    }

    @Test
    @DisplayName("가게 목록 조회 - 카테고리 필터링 성공")
    void getStoreList_withCategory() throws Exception {
        StoreListResponse.StoreListItemResponse item = new StoreListResponse.StoreListItemResponse(
                2L, "중식당", "중식", "https://cdn.example.com/thumb2.jpg", 3.8, 5L, 3.6);
        StoreListResponse response = new StoreListResponse(List.of(item));

        given(storeService.getStoreList("중식")).willReturn(response);

        mockMvc.perform(get("/v1/shops").param("category", "중식"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stores.length()").value(1))
                .andExpect(jsonPath("$.stores[0].name").value("중식당"))
                .andExpect(jsonPath("$.stores[0].category").value("중식"));

        verify(storeService).getStoreList("중식");
    }

    @Test
    @DisplayName("가게 목록 조회 - 가게가 없는 경우 빈 배열 반환")
    void getStoreList_empty() throws Exception {
        given(storeService.getStoreList(null)).willReturn(new StoreListResponse(List.of()));

        mockMvc.perform(get("/v1/shops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stores").isArray())
                .andExpect(jsonPath("$.stores.length()").value(0));
    }

    @Test
    @DisplayName("가게 목록 조회 - weightedRating 필드는 응답에 포함되지 않음")
    void getStoreList_weightedRatingNotExposed() throws Exception {
        StoreListResponse.StoreListItemResponse item = new StoreListResponse.StoreListItemResponse(
                1L, "맛집1", "한식", "https://cdn.example.com/thumb1.jpg", 4.5, 10L, 4.3);
        given(storeService.getStoreList(null)).willReturn(new StoreListResponse(List.of(item)));

        mockMvc.perform(get("/v1/shops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stores[0].weightedRating").doesNotExist());
    }

    // ───────────────────────────────────────────────
    // GET /v1/shops/{shopId}
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("가게 상세 조회 - 성공")
    void getStoreInfo_success() throws Exception {
        StoreInfoResponse response = new StoreInfoResponse(1L, "맛집1", "한식", "02-1234-5678", "서울시 강남구 테헤란로 1");

        given(storeService.getStoreInfo(1L)).willReturn(response);

        mockMvc.perform(get("/v1/shops/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("맛집1"))
                .andExpect(jsonPath("$.category").value("한식"))
                .andExpect(jsonPath("$.tel").value("02-1234-5678"))
                .andExpect(jsonPath("$.address").value("서울시 강남구 테헤란로 1"));

        verify(storeService).getStoreInfo(1L);
    }

    @Test
    @DisplayName("가게 상세 조회 - 존재하지 않는 가게 조회 시 IllegalArgumentException 발생")
    void getStoreInfo_notFound() throws Exception {
        given(storeService.getStoreInfo(999L))
                .willThrow(new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

        ServletException ex = assertThrows(ServletException.class, () ->
                mockMvc.perform(get("/v1/shops/999")));
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());

        verify(storeService).getStoreInfo(999L);
    }
}
