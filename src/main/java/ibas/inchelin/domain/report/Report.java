package ibas.inchelin.domain.report;


import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.review.Review;
import ibas.inchelin.domain.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime handledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
}
