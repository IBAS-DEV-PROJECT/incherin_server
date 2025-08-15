package ibas.inchelin.domain.user.repository;

import ibas.inchelin.domain.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}

