package ibas.inchelin.domain.user;

import ibas.inchelin.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "follow_list",
        uniqueConstraints = @UniqueConstraint(name = "uk_follow_follower_followee", columnNames = {"follower_id", "followee_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팔로우를 거는 사람
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private User user;

    // 팔로우 대상
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id")
    private User followUser;
}