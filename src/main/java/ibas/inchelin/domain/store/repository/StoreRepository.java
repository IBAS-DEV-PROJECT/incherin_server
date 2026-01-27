package ibas.inchelin.domain.store.repository;

import ibas.inchelin.domain.store.Category;
import ibas.inchelin.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // 가게 카테고리 별 조회
    List<Store> findByCategory(Category category);
}

