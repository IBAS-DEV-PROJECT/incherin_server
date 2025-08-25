package ibas.inchelin.domain.user.repository;

import ibas.inchelin.domain.user.entity.RefreshToken;
import ibas.inchelin.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    Optional<RefreshToken> findByUserAndIsActive(User user, Boolean isActive);
}
