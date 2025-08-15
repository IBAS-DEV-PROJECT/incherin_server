package ibas.inchelin.domain.user.repository;

import ibas.inchelin.domain.user.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {
}

