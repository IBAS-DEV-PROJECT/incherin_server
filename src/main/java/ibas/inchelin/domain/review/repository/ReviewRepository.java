package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 작성자 기준 최신순
    List<Review> findByWrittenBy_SubOrderByCreatedAtDesc(String sub);
    // 작성자 기준 오래된순
    List<Review> findByWrittenBy_SubOrderByCreatedAtAsc(String sub);
    // 작성자 기준 평점높은순
    List<Review> findByWrittenBy_SubOrderByRatingDesc(String sub);
    // 방문 횟수
    int countByWrittenBy_IdAndStoreId(Long userId, Long storeId);
}

