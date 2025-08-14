package ibas.inchelin.domain.review;

import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.store.Menu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_menu",
        uniqueConstraints = @UniqueConstraint(name = "uk_reviewmenu_review_menu", columnNames = {"review_id", "menu_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewMenu extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;
}
