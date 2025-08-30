package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.ReviewKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewKeywordRepository extends JpaRepository<ReviewKeyword, Long> {
    List<ReviewKeyword> findByReviewId(Long reviewId);
}

