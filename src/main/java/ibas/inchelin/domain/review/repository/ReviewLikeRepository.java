package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    // 리뷰 좋아요 개수
    int countByReviewId(Long reviewId);
}
