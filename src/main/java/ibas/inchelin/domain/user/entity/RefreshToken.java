package ibas.inchelin.domain.user.entity;


import ibas.inchelin.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token",
        uniqueConstraints = @UniqueConstraint(name = "uk_refresh_token_value", columnNames = "refreshToken")
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 512, unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    // 토큰 재발급/갱신 도메인 메서드
    public void renew(String newToken, LocalDateTime now, LocalDateTime newExpiresAt) {
        this.refreshToken = newToken;
        this.createdAt = now;
        this.expiresAt = newExpiresAt;
        this.isActive = true;
    }

    // 비활성화 도메인 메서드
    public void deactivate() {
        this.isActive = false;
    }
}