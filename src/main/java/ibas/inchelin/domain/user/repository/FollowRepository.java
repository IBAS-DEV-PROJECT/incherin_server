package ibas.inchelin.domain.user.repository;

import ibas.inchelin.domain.user.entity.Follow;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<Follow> findByFollowUserId(Long userId);

    @EntityGraph(attributePaths = {"followUser"})
    List<Follow> findByUserId(Long userId);
}