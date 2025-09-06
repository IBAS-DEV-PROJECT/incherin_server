package ibas.inchelin.web.controller;

import ibas.inchelin.domain.review.service.ReviewService;
import ibas.inchelin.web.dto.review.ReviewListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/users/me/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewListResponse> getMyReviews(Authentication authentication, @RequestParam(required = false, defaultValue = "latest") String sort) {
        return ResponseEntity.ok(reviewService.getMyReviews(authentication.getName(), sort));
    }
}
