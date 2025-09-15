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

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final S3Service s3Service;

    @GetMapping("/users/me/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewListResponse> getMyReviews(Authentication authentication, @RequestParam(required = false, defaultValue = "latest") String sort) {
        return ResponseEntity.ok(reviewService.getMyReviews(authentication.getName(), sort));
    }

    @PostMapping(value = "/users/me/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> writeReview(Authentication authentication, ReviewWriteRequest writeRequest) throws IOException {
        List<String> photoUrls = s3Service.uploadMany(writeRequest.getPhotos());
        reviewService.write(authentication.getName(), writeRequest.getStoreId(), writeRequest.getRating(), writeRequest.getContent(),
                writeRequest.getEatingMenus(), writeRequest.getKeywords(), photoUrls);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/users/me/reviews/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMyReview(Authentication authentication, @PathVariable Long reviewId) {
        reviewService.deleteMyReview(authentication.getName(), reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviews/stores/{storeId}")
    public ResponseEntity<ReviewListResponse> getStoreReviews(@PathVariable Long storeId, @RequestParam(required = false, defaultValue = "latest") String sort) {
        return ResponseEntity.ok(reviewService.getStoreReviews(storeId, sort));
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
