package ibas.inchelin.domain.review.service;

import ibas.inchelin.domain.review.Keyword;
import ibas.inchelin.domain.review.entity.*;
import ibas.inchelin.domain.review.repository.*;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.UserRepository;
import ibas.inchelin.web.dto.review.ReviewListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final ReviewMenuRepository reviewMenuRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

//    @Transactional(readOnly = true)
//    public ReviewListResponse getMyReviews(String sub, String sort) {
//        List<Review> reviews;
//        if ("rating".equalsIgnoreCase(sort)) { // 평점높은순
//            reviews = reviewRepository.findByWrittenBy_SubOrderByRatingDesc(sub);
//        } else if ("oldest".equalsIgnoreCase(sort)) { // 오래된순
//            reviews = reviewRepository.findByWrittenBy_SubOrderByCreatedAtAsc(sub);
//        } else { // 최신순
//            reviews = reviewRepository.findByWrittenBy_SubOrderByCreatedAtDesc(sub);
//        }
//
//        return getReviewListResponse(reviews);
//    }

    // 리뷰 등록
    public ReviewListResponse.ReviewResponse write(Long storeId, String nickname, Double rating, String content, List<String> photoUrls) {
        Store store = storeRepository.findById(storeId).orElseThrow();

        Review review = Review.builder()
                .rating(rating)
                .content(content)
                .store(store)
                .writtenBy(nickname)
                .build();

        if (photoUrls != null) {
            List<ReviewPhoto> photos = photoUrls.stream()
                    .map(url -> ReviewPhoto.builder().imageUrl(url).build())
                    .toList();
            review.addReviewPhoto(photos);
        }
        Review created = reviewRepository.save(review);

        return new ReviewListResponse.ReviewResponse(
                created.getId(),
                created.getWrittenBy(),
                created.getRating(),
                created.getContent(),
                reviewPhotoRepository.findByReviewId(created.getId()).stream().map(ReviewPhoto::getImageUrl).toList(),
                created.getCreatedAt().toInstant(ZoneOffset.UTC));
    }

    public void deleteMyReview(String sub, Long reviewId) {
        User user = userRepository.findBySub(sub).orElseThrow();
        Review review = reviewRepository.findById(reviewId).orElseThrow();
//        if (!review.getWrittenBy().getId().equals(user.getId())) {
//            throw new IllegalArgumentException("본인의 리뷰만 삭제할 수 있습니다.");
//        }
        reviewRepository.delete(review);
    }

    // 리뷰 목록 조회
    @Transactional(readOnly = true)
    public ReviewListResponse getStoreReviews(Long storeId) {
        List<Review> reviews = reviewRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
        return getReviewListResponse(reviews);
    }

    public void likeReview(String sub, Long reviewId) {
        User user = userRepository.findBySub(sub).orElseThrow();
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        ReviewLike reviewLike = ReviewLike.builder()
                .review(review)
                .user(user)
                .build();
        reviewLikeRepository.save(reviewLike);
    }

    public void unlikeReview(String sub, Long reviewId) {
        User user = userRepository.findBySub(sub).orElseThrow();
        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdAndUserId(reviewId, user.getId());
        reviewLikeRepository.delete(reviewLike);
    }

    private ReviewListResponse getReviewListResponse(List<Review> reviews) {
        List<ReviewListResponse.ReviewResponse> reviewList = reviews.stream()
                .map(r -> new ReviewListResponse.ReviewResponse(
                        r.getId(),
                        r.getWrittenBy(),
                        r.getRating(),
                        r.getContent(),
                        reviewPhotoRepository.findByReviewId(r.getId()).stream().map(ReviewPhoto::getImageUrl).toList(),
                        r.getCreatedAt().toInstant(ZoneOffset.UTC)))
                .toList();
        return new ReviewListResponse(reviewList);
    }
}
