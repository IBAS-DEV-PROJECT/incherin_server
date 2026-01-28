package ibas.inchelin.domain.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_nickname_adj")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewNicknameAdj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 형용사
    @Column(nullable = false)
    private String adjective;
}
