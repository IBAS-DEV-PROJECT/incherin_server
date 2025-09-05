package ibas.inchelin.domain.user.entity;

import ibas.inchelin.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "like_list",
        uniqueConstraints = @UniqueConstraint(name = "uk_likelist_user_name", columnNames = {"user_id", "name"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeList extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "likeList", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LikeListStore> likeListStores = new ArrayList<>();

    @Builder
    public LikeList(String name, User user) {
        this.name = name;
        this.user = user;
    }
}