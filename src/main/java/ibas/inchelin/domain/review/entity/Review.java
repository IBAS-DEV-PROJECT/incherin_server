package ibas.inchelin.domain.review.entity;

import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.store.entity.Store;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private String writtenBy;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewPhoto> reviewPhotos = new ArrayList<>();

    @Builder
    public Review(Double rating, String content, Store store, String writtenBy) {
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
}
