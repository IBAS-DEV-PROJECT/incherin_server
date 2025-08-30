package ibas.inchelin.domain.user.repository;

import ibas.inchelin.domain.user.entity.LikeList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeListRepository extends JpaRepository<LikeList, Long> {
    List<LikeList> findByUserId(Long id);
}

