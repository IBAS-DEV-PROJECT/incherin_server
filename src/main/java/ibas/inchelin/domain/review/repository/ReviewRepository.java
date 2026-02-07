package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 가게로 조회, 최신순
    @Query("""
           select distinct r
           from Review r
           left join fetch r.reviewPhotos rp
           where r.store.id = :storeId
           order by r.createdAt desc
           """)
    List<Review> findByStoreIdOrderByCreatedAtDescWithPhotos(Long storeId);
}

