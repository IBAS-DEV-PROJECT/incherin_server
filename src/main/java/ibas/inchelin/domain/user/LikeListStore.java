package ibas.inchelin.domain.user;

import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.store.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "like_list_store",
        uniqueConstraints = @UniqueConstraint(name = "uk_likeliststore_list_store", columnNames = {"list_id", "store_id"}),
        indexes = {
                @Index(name = "idx_likeliststore_list", columnList = "list_id"),
                @Index(name = "idx_likeliststore_store", columnList = "store_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeListStore extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private LikeList likeList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
}