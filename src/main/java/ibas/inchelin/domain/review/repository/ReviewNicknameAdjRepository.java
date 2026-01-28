package ibas.inchelin.domain.review.repository;

import ibas.inchelin.domain.review.entity.ReviewNicknameAdj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewNicknameAdjRepository extends JpaRepository<ReviewNicknameAdj, Long> {

    // 랜덤으로 한 개 조회
    @Query(value = "SELECT * FROM review_nickname_adj ORDER BY RAND() LIMIT 1", nativeQuery = true)
    ReviewNicknameAdj findRandomOne();
}
