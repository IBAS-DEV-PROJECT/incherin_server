package ibas.inchelin.domain.store.repository;

import ibas.inchelin.domain.store.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}

