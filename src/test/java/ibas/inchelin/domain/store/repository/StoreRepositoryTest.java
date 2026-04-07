package ibas.inchelin.domain.store.repository;

import ibas.inchelin.domain.review.entity.Review;
import ibas.inchelin.domain.store.Category;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.web.dto.store.StoreListResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(StoreRepositoryImpl.class)
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private EntityManager em;

    // ───────────────────────────────────────────────
    // 헬퍼
    // ───────────────────────────────────────────────

    private Store saveStore(String placeName, Category category) throws Exception {
        Constructor<Store> ctor = Store.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        Store store = ctor.newInstance();
        ReflectionTestUtils.setField(store, "placeName", placeName);
        ReflectionTestUtils.setField(store, "categoryName", category);
        ReflectionTestUtils.setField(store, "roadAddressName", placeName + " 주소");
        em.persist(store);
        return store;
    }

    private void saveReview(Store store, double rating) {
        Review review = Review.builder()
                .store(store)
                .rating(rating)
                .content("리뷰 내용")
                .writtenBy("tester")
                .build();
        em.persist(review);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    // ───────────────────────────────────────────────
    // findStoreListWithStatistics
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("카테고리 없이 전체 가게 목록 조회")
    void findStoreListWithStatistics_noCategory() throws Exception {
        Store korean = saveStore("한식당", Category.KOREAN);
        Store chinese = saveStore("중식당", Category.CHINESE);
        saveReview(korean, 4.0);
        saveReview(korean, 5.0);
        flushAndClear();

        List<StoreListResponse.StoreListItemResponse> result =
                storeRepository.findStoreListWithStatistics(null);

        assertThat(result).hasSize(2);
        List<Long> ids = result.stream().map(StoreListResponse.StoreListItemResponse::id).toList();
        assertThat(ids).containsExactlyInAnyOrder(korean.getId(), chinese.getId());
    }

    @Test
    @DisplayName("카테고리로 필터링하면 해당 카테고리 가게만 반환")
    void findStoreListWithStatistics_withCategory() throws Exception {
        Store korean = saveStore("한식당", Category.KOREAN);
        saveStore("중식당", Category.CHINESE);
        saveReview(korean, 4.0);
        flushAndClear();

        List<StoreListResponse.StoreListItemResponse> result =
                storeRepository.findStoreListWithStatistics(Category.KOREAN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(korean.getId());
    }

    @Test
    @DisplayName("등록된 가게가 없으면 빈 목록 반환")
    void findStoreListWithStatistics_empty() {
        List<StoreListResponse.StoreListItemResponse> result =
                storeRepository.findStoreListWithStatistics(null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("리뷰 없는 가게는 리뷰 있는 가게보다 뒤에 정렬")
    void findStoreListWithStatistics_reviewedStoreComesFirst() throws Exception {
        Store noReviewStore = saveStore("리뷰없는가게", Category.KOREAN);
        Store reviewedStore = saveStore("리뷰있는가게", Category.KOREAN);
        saveReview(reviewedStore, 4.0);
        flushAndClear();

        List<StoreListResponse.StoreListItemResponse> result =
                storeRepository.findStoreListWithStatistics(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(reviewedStore.getId());
        assertThat(result.get(1).id()).isEqualTo(noReviewStore.getId());
    }

    @Test
    @DisplayName("리뷰 없는 가게의 평균 평점은 0.0, 리뷰 수는 0")
    void findStoreListWithStatistics_noReviewStoreDefaultValues() throws Exception {
        saveStore("리뷰없는가게", Category.CAFE);
        flushAndClear();

        List<StoreListResponse.StoreListItemResponse> result =
                storeRepository.findStoreListWithStatistics(null);

        assertThat(result).hasSize(1);
        StoreListResponse.StoreListItemResponse item = result.get(0);
        assertThat(item.averageRating()).isEqualTo(0.0);
        assertThat(item.reviewCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("averageRating은 소수점 2자리로 반환")
    void findStoreListWithStatistics_averageRatingRoundedToTwoDecimalPlaces() throws Exception {
        Store store = saveStore("맛집", Category.KOREAN);
        saveReview(store, 4.0);
        saveReview(store, 4.0);
        saveReview(store, 5.0);
        flushAndClear();

        List<StoreListResponse.StoreListItemResponse> result =
                storeRepository.findStoreListWithStatistics(null);

        assertThat(result).hasSize(1);
        // (4.0 + 4.0 + 5.0) / 3 = 4.3333...
        assertThat(result.get(0).averageRating()).isEqualTo(4.33);
    }

    @Test
    @DisplayName("가중 평균이 높은 가게가 먼저 정렬")
    void findStoreListWithStatistics_orderedByWeightedRatingDesc() throws Exception {
        Store lowRatingStore = saveStore("저평점가게", Category.KOREAN);
        Store highRatingStore = saveStore("고평점가게", Category.KOREAN);
        // 저평점: 리뷰 3개, 평균 2.0
        saveReview(lowRatingStore, 2.0);
        saveReview(lowRatingStore, 2.0);
        saveReview(lowRatingStore, 2.0);
        // 고평점: 리뷰 3개, 평균 5.0
        saveReview(highRatingStore, 5.0);
        saveReview(highRatingStore, 5.0);
        saveReview(highRatingStore, 5.0);
        flushAndClear();

        List<StoreListResponse.StoreListItemResponse> result =
                storeRepository.findStoreListWithStatistics(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(highRatingStore.getId());
        assertThat(result.get(1).id()).isEqualTo(lowRatingStore.getId());
    }

    @Test
    @DisplayName("DTO의 name, reviewCount, averageRating 필드가 올바르게 매핑")
    void findStoreListWithStatistics_dtoFieldMapping() throws Exception {
        Store store = saveStore("테스트가게", Category.JAPANESE);
        saveReview(store, 4.0);
        saveReview(store, 4.0);
        flushAndClear();

        List<StoreListResponse.StoreListItemResponse> result =
                storeRepository.findStoreListWithStatistics(null);

        assertThat(result).hasSize(1);
        StoreListResponse.StoreListItemResponse item = result.get(0);
        assertThat(item.name()).isEqualTo("테스트가게");
        assertThat(item.category()).isEqualTo(Category.JAPANESE.displayName());
        assertThat(item.reviewCount()).isEqualTo(2L);
        assertThat(item.averageRating()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("카테고리 필터 시 C(전체 평균 평점)는 해당 카테고리 내 리뷰만으로 계산")
    void findStoreListWithStatistics_categoryFilterIsolatesAverageC() throws Exception {
        // 한식당: 리뷰 2개, 평균 4.0 → R=4.0, v=2, m=2
        // 카테고리 KOREAN 조건 시 C = 한식 리뷰 평균 = 4.0
        // weightedRating = 2/4 * 4.0 + 2/4 * 4.0 = 4.0
        Store koreanStore = saveStore("한식당", Category.KOREAN);
        saveReview(koreanStore, 4.0);
        saveReview(koreanStore, 4.0);

        // 일식당: 리뷰 100개, 평균 1.0 (C를 전체 평균으로 계산하면 한식당 weightedRating이 ~2.0 수준으로 낮아짐)
        Store japaneseStore = saveStore("일식당", Category.JAPANESE);
        for (int i = 0; i < 100; i++) {
            saveReview(japaneseStore, 1.0);
        }
        flushAndClear();

        List<StoreListResponse.StoreListItemResponse> result =
                storeRepository.findStoreListWithStatistics(Category.KOREAN);

        assertThat(result).hasSize(1);
        // C가 KOREAN 내 평균(4.0)으로 계산되면 weightedRating = 4.0
        // C가 전체 평균(≈1.04)으로 계산되면 weightedRating ≈ 2.52 (명백히 다름)
        assertThat(result.get(0).weightedRating()).isGreaterThan(3.5);
    }
}
