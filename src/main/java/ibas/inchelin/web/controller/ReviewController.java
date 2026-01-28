package ibas.inchelin.web.controller;

import ibas.inchelin.S3Service;
import ibas.inchelin.domain.review.service.ReviewService;
import ibas.inchelin.web.dto.review.ReviewListResponse;
import ibas.inchelin.web.dto.review.ReviewWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final S3Service s3Service;

//    @GetMapping("/users/me/reviews")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<ReviewListResponse> getMyReviews(Authentication authentication, @RequestParam(required = false, defaultValue = "latest") String sort) {
//        return ResponseEntity.ok(reviewService.getMyReviews(authentication.getName(), sort));
//    }

    // 리뷰 등록
    @PostMapping(value = "/v1/shops/{shopId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewListResponse.ReviewResponse> writeReview(
            @ModelAttribute ReviewWriteRequest writeRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @PathVariable Long shopId) throws IOException {
        List<String> photoUrls;

        photoUrls = images != null && !images.isEmpty() && !images.get(0).isEmpty() ? s3Service.uploadMany(images) : null;
        ReviewListResponse.ReviewResponse created = reviewService.write(shopId, writeRequest.getNickname(), writeRequest.getRating(), writeRequest.getContent(), photoUrls);

        URI location = URI.create("/v1/shops/" + shopId + "/reviews/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/users/me/reviews/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMyReview(Authentication authentication, @PathVariable Long reviewId) {
        reviewService.deleteMyReview(authentication.getName(), reviewId);
        return ResponseEntity.noContent().build();
    }

    // 리뷰 목록 조회
    @GetMapping("/v1/shops/{shopId}/reviews")
    public ResponseEntity<ReviewListResponse> getStoreReviews(@PathVariable Long shopId) {
        return ResponseEntity.ok(reviewService.getStoreReviews(shopId));
    }

    @PostMapping("/users/reviews/{reviewId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> likeReview(Authentication authentication, @PathVariable Long reviewId) {
        reviewService.likeReview(authentication.getName(), reviewId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/users/reviews/{reviewId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unlikeReview(Authentication authentication, @PathVariable Long reviewId) {
        reviewService.unlikeReview(authentication.getName(), reviewId);
        return ResponseEntity.noContent().build();
    }
}
