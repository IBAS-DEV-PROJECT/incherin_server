package ibas.inchelin.domain.notice.entity;

import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.notice.NoticeType;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.Visibility;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id // 이 필드가 테이블의 기본 키(Primary Key)임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값을 데이터베이스가 자동으로 생성하도록 함
    private Long id;

    @Column(nullable = false)
    private String title; // 목록 조회 시 사용할 제목

    @Lob
    private String content; // 목록 조회 시 사용할 본문

    @Enumerated(EnumType.STRING)
    private NoticeType type;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "written_by")
    private User writtenBy;
}