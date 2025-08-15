package ibas.inchelin.domain.inquiry.entity;

import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.Status;
import ibas.inchelin.domain.inquiry.InquiryType;
import ibas.inchelin.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private InquiryType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "written_by")
    private User writtenBy;
}
