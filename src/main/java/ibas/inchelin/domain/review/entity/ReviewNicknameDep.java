package ibas.inchelin.domain.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_nickname_dep")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewNicknameDep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 학과
    @Column(nullable = false)
    private String department;
}
