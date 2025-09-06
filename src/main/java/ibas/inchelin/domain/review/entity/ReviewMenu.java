package ibas.inchelin.domain.review.entity;

import ibas.inchelin.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewMenu extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    public ReviewMenu(String menu, Review review) {
        this.menu = menu;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
