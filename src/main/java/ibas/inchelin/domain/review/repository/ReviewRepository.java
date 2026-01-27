package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 가게로 조회, 최신순
    List<Review> findByStoreIdOrderByCreatedAtDesc(Long storeId);
}

