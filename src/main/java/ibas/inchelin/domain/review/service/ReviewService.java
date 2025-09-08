package ibas.inchelin.domain.review.service;

import ibas.inchelin.domain.review.Keyword;
import ibas.inchelin.domain.review.entity.Review;
import ibas.inchelin.domain.review.entity.ReviewKeyword;
import ibas.inchelin.domain.review.entity.ReviewMenu;
import ibas.inchelin.domain.review.entity.ReviewPhoto;
import ibas.inchelin.domain.review.repository.*;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.UserRepository;
import ibas.inchelin.web.dto.review.ReviewListResponse;
import ibas.inchelin.web.dto.review.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public ReviewListResponse getMyReviews(String sub, String sort) {
        List<Review> reviews;
        if ("rating".equalsIgnoreCase(sort)) { // 평점높은순
            reviews = reviewRepository.findByWrittenBy_SubOrderByRatingDesc(sub);
        } else if ("oldest".equalsIgnoreCase(sort)) { // 오래된순
            reviews = reviewRepository.findByWrittenBy_SubOrderByCreatedAtAsc(sub);
        } else { // 최신순
            reviews = reviewRepository.findByWrittenBy_SubOrderByCreatedAtDesc(sub);
        }

        List<ReviewResponse> reviewList = reviews.stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getWrittenBy().getId(),
                        r.getRating(),
                        reviewMenuRepository.findByReviewId(r.getId()).stream().map(ReviewMenu::getMenu).toList(),
                        reviewPhotoRepository.findByReviewId(r.getId()).stream().map(ReviewPhoto::getImageUrl).toList(),
                        r.getContent(),
                        reviewKeywordRepository.findByReviewId(r.getId()).stream().map(rk -> rk.getKeyword().getLabel()).toList(),
                        r.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        reviewRepository.countByWrittenBy_IdAndStoreId(r.getWrittenBy().getId(), r.getStore().getId()),
                        reviewLikeRepository.countByReviewId(r.getId()),
                        false))
                .toList();
        return new ReviewListResponse(reviewList);
    }

    public void write(String sub, Long storeId, Double rating, String content, List<String> eatingMenus, List<Keyword> keywords, List<String> photoUrls) {
        User user = userRepository.findBySub(sub).orElseThrow();
        Store store = storeRepository.findById(storeId).orElseThrow();
        List<ReviewPhoto> photos = photoUrls.stream()
                .map(url -> ReviewPhoto.builder().imageUrl(url).build())
                .toList();
        List<ReviewKeyword> keywordList = keywords.stream()
                .map(keyword -> ReviewKeyword.builder().keyword(keyword).build())
                .toList();
        List<ReviewMenu> menus = eatingMenus.stream()
                .map(menu -> ReviewMenu.builder().menu(menu).build())
                .toList();

        Review review = Review.builder()
                .rating(rating)
                .content(content)
                .store(store)
                .writtenBy(user)
                .build();
        review.addReviewPhoto(photos);
        review.addReviewKeyword(keywordList);
        review.addReviewMenu(menus);
        reviewRepository.save(review);
    }

    public void deleteMyReview(String sub, Long reviewId) {
        User user = userRepository.findBySub(sub).orElseThrow();
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        if (!review.getWrittenBy().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 리뷰만 삭제할 수 있습니다.");
        }
        reviewRepository.delete(review);
    }
}
