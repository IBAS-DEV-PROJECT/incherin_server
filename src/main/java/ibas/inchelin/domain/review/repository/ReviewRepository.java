package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 작성자로 조회, 최신순
    List<Review> findByWrittenBy_SubOrderByCreatedAtDesc(String sub);
    // 작성자로 조회, 오래된순
    List<Review> findByWrittenBy_SubOrderByCreatedAtAsc(String sub);
    // 작성자로 조회, 평점높은순
    List<Review> findByWrittenBy_SubOrderByRatingDesc(String sub);

    // 방문 횟수
    int countByWrittenBy_IdAndStoreId(Long userId, Long storeId);

    // 가게로 조회, 최신순
    List<Review> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    // 가게로 조회, 오래된순
    List<Review> findByStoreIdOrderByCreatedAtAsc(Long storeId);
    // 가게로 조회, 평점높은순
    List<Review> findByStoreIdOrderByRatingDesc(Long storeId);
}

