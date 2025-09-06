package ibas.inchelin.domain.review.entity;

import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.review.VisitType;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double rating;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private VisitType visitType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "written_by")
    private User writtenBy;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewPhoto> reviewPhotos = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewKeyword> reviewKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewMenu> reviewMenus = new ArrayList<>();

    @Builder
    public Review(Double rating, String content, Store store, User writtenBy) {
        this.rating = rating;
        this.content = content;
        this.store = store;
        this.writtenBy = writtenBy;
    }

    public void addReviewPhoto(List<ReviewPhoto> reviewPhoto) {
        for (ReviewPhoto photo : reviewPhoto) {
            photo.setReview(this);
            this.reviewPhotos.add(photo);
        }
    }

    public void addReviewKeyword(List<ReviewKeyword> reviewKeyword) {
        for (ReviewKeyword keyword : reviewKeyword) {
            keyword.setReview(this);
            this.reviewKeywords.add(keyword);
        }
    }

    public void addReviewMenu(List<ReviewMenu> reviewMenu) {
        for (ReviewMenu menu : reviewMenu) {
            menu.setReview(this);
            this.reviewMenus.add(menu);
        }
    }
}
