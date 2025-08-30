package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.ReviewMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewMenuRepository extends JpaRepository<ReviewMenu, Long> {
    List<ReviewMenu> findByReviewId(Long reviewId);
}
