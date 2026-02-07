package ibas.inchelin.domain.review.service;

import ibas.inchelin.domain.review.entity.*;
import ibas.inchelin.domain.review.repository.*;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.web.dto.review.ReviewListResponse;
import ibas.inchelin.web.dto.review.ReviewNicknameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final StoreRepository storeRepository;
    private final ReviewNicknameAdjRepository reviewNicknameAdjRepository;
    private final ReviewNicknameDepRepository reviewNicknameDepRepository;

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
                created.getRating().intValue(),
                created.getContent(),
                reviewPhotoRepository.findByReviewId(created.getId()).stream().map(ReviewPhoto::getImageUrl).toList(),
                created.getCreatedAt().toInstant(ZoneOffset.UTC));
    }

    // 리뷰 목록 조회
    @Transactional(readOnly = true)
    public ReviewListResponse getStoreReviews(Long storeId) {
        List<Review> reviews = reviewRepository.findByStoreIdOrderByCreatedAtDescWithPhotos(storeId);
        return getReviewListResponse(reviews);
    }


    private ReviewListResponse getReviewListResponse(List<Review> reviews) {
        List<ReviewListResponse.ReviewResponse> reviewList = reviews.stream()
                .map(r -> new ReviewListResponse.ReviewResponse(
                        r.getId(),
                        r.getWrittenBy(),
                        r.getRating().intValue(),
                        r.getContent(),
                        r.getReviewPhotos() == null ? List.of() : r.getReviewPhotos().stream().map(ReviewPhoto::getImageUrl).toList(),
                        r.getCreatedAt().toInstant(ZoneOffset.UTC)))
                .toList();
        return new ReviewListResponse(reviewList);
    }

    // 랜덤 닉네임 조회
    @Transactional(readOnly = true)
    public ReviewNicknameResponse getRandomNickname() {
        ReviewNicknameAdj randomAdj = reviewNicknameAdjRepository.findRandomOne();
        ReviewNicknameDep randomDep = reviewNicknameDepRepository.findRandomOne();

        String nickname = randomAdj.getAdjective() + " " + randomDep.getDepartment();
        return new ReviewNicknameResponse(nickname);
    }
}
