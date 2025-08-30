package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {
    List<ReviewPhoto> findByReviewId(Long reviewId);
}

