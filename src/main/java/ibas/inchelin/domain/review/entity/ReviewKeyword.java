package ibas.inchelin.domain.review.entity;

import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.review.Keyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_keyword",
        uniqueConstraints = @UniqueConstraint(name = "uk_reviewkeyword_review_keyword", columnNames = {"review_id", "keyword"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewKeyword extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Keyword keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    public ReviewKeyword(Keyword keyword, Review review) {
        this.keyword = keyword;
        this.review = review;
    }
}
