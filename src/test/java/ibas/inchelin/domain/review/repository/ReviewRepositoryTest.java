package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.Review;
import ibas.inchelin.domain.review.entity.ReviewPhoto;
import ibas.inchelin.domain.store.Category;
import ibas.inchelin.domain.store.entity.Store;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager em;

    // ───────────────────────────────────────────────
    // 헬퍼
    // ───────────────────────────────────────────────

    private Store saveStore(String placeName) throws Exception {
        Constructor<Store> ctor = Store.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        Store store = ctor.newInstance();
        ReflectionTestUtils.setField(store, "placeName", placeName);
        ReflectionTestUtils.setField(store, "categoryName", Category.KOREAN);
        ReflectionTestUtils.setField(store, "roadAddressName", placeName + " 주소");
        em.persist(store);
        return store;
    }

    private Review saveReview(Store store, String content) {
        Review review = Review.builder()
                .store(store)
                .rating(4.0)
                .content(content)
                .writtenBy("tester")
                .build();
        em.persist(review);
        return review;
    }

    private void saveReviewPhoto(Review review, String imageUrl) {
        ReviewPhoto photo = ReviewPhoto.builder()
                .imageUrl(imageUrl)
                .build();
        photo.setReview(review);
        em.persist(photo);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    // ───────────────────────────────────────────────
    // findByStoreIdOrderByCreatedAtDescWithPhotos
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("해당 가게의 리뷰 목록을 최신순으로 반환")
    void findByStoreId_returnsReviewsOrderedByCreatedAtDesc() throws Exception {
        Store store = saveStore("맛집");
        Review first = saveReview(store, "첫 번째 리뷰");
        Review second = saveReview(store, "두 번째 리뷰");
        em.flush();
        // updatable=false 컬럼은 JPA dirty check 대상이 아니므로 네이티브 쿼리로 직접 설정
        em.createNativeQuery("UPDATE review SET created_at = ? WHERE id = ?")
                .setParameter(1, java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(1)))
                .setParameter(2, first.getId())
                .executeUpdate();
        em.createNativeQuery("UPDATE review SET created_at = ? WHERE id = ?")
                .setParameter(1, java.sql.Timestamp.valueOf(LocalDateTime.now()))
                .setParameter(2, second.getId())
                .executeUpdate();
        em.clear();

        List<Review> result = reviewRepository.findByStoreIdOrderByCreatedAtDescWithPhotos(store.getId());

        assertThat(result).hasSize(2);
        List<Long> ids = result.stream().map(Review::getId).toList();
        assertThat(ids).containsExactly(second.getId(), first.getId());
    }

    @Test
    @DisplayName("다른 가게의 리뷰는 포함되지 않음")
    void findByStoreId_excludesOtherStoreReviews() throws Exception {
        Store target = saveStore("타겟가게");
        Store other = saveStore("다른가게");
        saveReview(target, "타겟 리뷰");
        saveReview(other, "다른 가게 리뷰");
        flushAndClear();

        List<Review> result = reviewRepository.findByStoreIdOrderByCreatedAtDescWithPhotos(target.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("타겟 리뷰");
    }

    @Test
    @DisplayName("리뷰가 없는 가게는 빈 목록 반환")
    void findByStoreId_emptyWhenNoReviews() throws Exception {
        Store store = saveStore("리뷰없는가게");
        flushAndClear();

        List<Review> result = reviewRepository.findByStoreIdOrderByCreatedAtDescWithPhotos(store.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 storeId는 빈 목록 반환")
    void findByStoreId_emptyWhenStoreNotExists() {
        List<Review> result = reviewRepository.findByStoreIdOrderByCreatedAtDescWithPhotos(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사진이 있는 리뷰는 reviewPhotos가 함께 로딩됨")
    void findByStoreId_fetchesReviewPhotos() throws Exception {
        Store store = saveStore("맛집");
        Review review = saveReview(store, "사진 있는 리뷰");
        saveReviewPhoto(review, "https://example.com/photo1.jpg");
        saveReviewPhoto(review, "https://example.com/photo2.jpg");
        flushAndClear();

        List<Review> result = reviewRepository.findByStoreIdOrderByCreatedAtDescWithPhotos(store.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReviewPhotos()).hasSize(2);
    }

    @Test
    @DisplayName("사진 없는 리뷰는 reviewPhotos가 빈 리스트")
    void findByStoreId_emptyPhotosWhenNoPhotos() throws Exception {
        Store store = saveStore("맛집");
        saveReview(store, "사진 없는 리뷰");
        flushAndClear();

        List<Review> result = reviewRepository.findByStoreIdOrderByCreatedAtDescWithPhotos(store.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReviewPhotos()).isEmpty();
    }

    @Test
    @DisplayName("사진이 여러 장인 리뷰가 중복 없이 단 1건으로 반환")
    void findByStoreId_noDuplicatesWithMultiplePhotos() throws Exception {
        Store store = saveStore("맛집");
        Review review = saveReview(store, "사진 많은 리뷰");
        saveReviewPhoto(review, "https://example.com/a.jpg");
        saveReviewPhoto(review, "https://example.com/b.jpg");
        saveReviewPhoto(review, "https://example.com/c.jpg");
        flushAndClear();

        List<Review> result = reviewRepository.findByStoreIdOrderByCreatedAtDescWithPhotos(store.getId());

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("반환된 리뷰의 writtenBy, rating, content 필드가 올바르게 매핑")
    void findByStoreId_fieldsMappedCorrectly() throws Exception {
        Store store = saveStore("맛집");
        Review review = Review.builder()
                .store(store)
                .rating(3.5)
                .content("상세 내용")
                .writtenBy("user123")
                .build();
        em.persist(review);
        flushAndClear();

        List<Review> result = reviewRepository.findByStoreIdOrderByCreatedAtDescWithPhotos(store.getId());

        assertThat(result).hasSize(1);
        Review found = result.get(0);
        assertThat(found.getRating()).isEqualTo(3.5);
        assertThat(found.getContent()).isEqualTo("상세 내용");
        assertThat(found.getWrittenBy()).isEqualTo("user123");
    }
}
