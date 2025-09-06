package ibas.inchelin.domain.review.service;

import ibas.inchelin.domain.review.entity.Review;
import ibas.inchelin.domain.review.entity.ReviewPhoto;
import ibas.inchelin.domain.review.repository.*;
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
                        reviewMenuRepository.findByReviewId(r.getId()).stream().map(rm -> new ReviewResponse.MenuNamePriceResponse(rm.getMenu().getName(), rm.getMenu().getPrice())).toList(),
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
}
