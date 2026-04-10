package ibas.inchelin.domain.store.service;

import ibas.inchelin.domain.store.Category;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.web.dto.store.StoreInfoResponse;
import ibas.inchelin.web.dto.store.StoreListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    // ───────────────────────────────────────────────
    // getStoreList
    // ───────────────────────────────────────────────

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("categoryStr이 null이거나 공백이면 category=null로 Repository 호출")
    void getStoreList_nullOrBlank_callsRepositoryWithNull(String categoryStr) {
        given(storeRepository.findStoreListWithStatistics(null)).willReturn(List.of());

        storeService.getStoreList(categoryStr);

        verify(storeRepository).findStoreListWithStatistics(null);
    }

    @Test
    @DisplayName("유효한 categoryStr이면 파싱된 Category로 Repository 호출")
    void getStoreList_validCategory_callsRepositoryWithParsedCategory() {
        given(storeRepository.findStoreListWithStatistics(Category.KOREAN)).willReturn(List.of());

        storeService.getStoreList("korean");

        verify(storeRepository).findStoreListWithStatistics(Category.KOREAN);
    }

    @Test
    @DisplayName("유효하지 않은 categoryStr이면 IllegalArgumentException 발생")
    void getStoreList_invalidCategory_throwsException() {
        assertThatThrownBy(() -> storeService.getStoreList("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 카테고리입니다.");
    }

    @Test
    @DisplayName("Repository 결과를 StoreListResponse로 감싸서 반환")
    void getStoreList_wrapsRepositoryResult() {
        StoreListResponse.StoreListItemResponse item = new StoreListResponse.StoreListItemResponse(
                1L, "한식당", Category.KOREAN, null, 4.5, 10L, 4.3
        );
        given(storeRepository.findStoreListWithStatistics(null)).willReturn(List.of(item));

        StoreListResponse response = storeService.getStoreList(null);

        assertThat(response.stores()).hasSize(1);
        assertThat(response.stores().get(0).id()).isEqualTo(1L);
        assertThat(response.stores().get(0).name()).isEqualTo("한식당");
    }

    // ───────────────────────────────────────────────
    // getStoreInfo
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("존재하는 storeId이면 StoreInfoResponse 반환")
    void getStoreInfo_existingId_returnsResponse() {
        Store store = makeStore(1L, "한식당", Category.KOREAN, "02-1234-5678", "서울시 중구");
        given(storeRepository.findById(1L)).willReturn(Optional.of(store));

        StoreInfoResponse response = storeService.getStoreInfo(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("한식당");
        assertThat(response.category()).isEqualTo(Category.KOREAN.displayName());
        assertThat(response.tel()).isEqualTo("02-1234-5678");
        assertThat(response.address()).isEqualTo("서울시 중구");
    }

    @Test
    @DisplayName("존재하지 않는 storeId이면 IllegalArgumentException 발생")
    void getStoreInfo_nonExistingId_throwsException() {
        given(storeRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> storeService.getStoreInfo(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 가게를 찾을 수 없습니다.");
    }

    // ───────────────────────────────────────────────
    // 헬퍼
    // ───────────────────────────────────────────────

    private Store makeStore(Long id, String placeName, Category category, String phone, String address) {
        try {
            Constructor<Store> ctor = Store.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            Store store = ctor.newInstance();
            ReflectionTestUtils.setField(store, "id", id);
            ReflectionTestUtils.setField(store, "placeName", placeName);
            ReflectionTestUtils.setField(store, "categoryName", category);
            ReflectionTestUtils.setField(store, "phone", phone);
            ReflectionTestUtils.setField(store, "roadAddressName", address);
            return store;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
