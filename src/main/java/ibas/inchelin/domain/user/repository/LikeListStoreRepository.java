package ibas.inchelin.domain.user.repository;

import ibas.inchelin.domain.user.entity.LikeListStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeListStoreRepository extends JpaRepository<LikeListStore, Long> {
    List<LikeListStore> findByLikeListId(Long id);
}
