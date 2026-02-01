package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 가게로 조회, 최신순
    List<Review> findByStoreIdOrderByCreatedAtDesc(Long storeId);

    // 평점 평균 조회
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.store.id = :storeId")
    Double findAverageRatingByStoreId(@Param("storeId") Long storeId);

    // 리뷰 개수 조회
    Long countByStoreId(Long storeId);
}

