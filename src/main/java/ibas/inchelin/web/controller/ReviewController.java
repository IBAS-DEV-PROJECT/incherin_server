package ibas.inchelin.web.controller;

import ibas.inchelin.domain.S3Service;
import ibas.inchelin.domain.review.service.ReviewService;
import ibas.inchelin.web.dto.review.ReviewListResponse;
import ibas.inchelin.web.dto.review.ReviewNicknameResponse;
import ibas.inchelin.web.dto.review.ReviewWriteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final S3Service s3Service;

    // 리뷰 등록
    @PostMapping(value = "/v1/shops/{shopId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewListResponse.ReviewResponse> writeReview(
            @ModelAttribute ReviewWriteRequest writeRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @PathVariable Long shopId) throws IOException {

        log.info("리뷰 작성 요청 - shopId: {}, nickname: {}, rating: {}",
                shopId, writeRequest.getNickname(), writeRequest.getRating());

        List<String> photoUrls;

        photoUrls = images != null && !images.isEmpty() && !images.get(0).isEmpty() ? s3Service.uploadMany(images) : null;
        ReviewListResponse.ReviewResponse created = reviewService.write(shopId, writeRequest.getNickname(), writeRequest.getRating(), writeRequest.getContent(), photoUrls);

        log.info("리뷰 작성 완료 - reviewId: {}", created.id());

        URI location = URI.create("/v1/shops/" + shopId + "/reviews/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    // 리뷰 목록 조회
    @GetMapping("/v1/shops/{shopId}/reviews")
    public ResponseEntity<ReviewListResponse> getStoreReviews(@PathVariable Long shopId) {
        log.info("리뷰 목록 조회 요청 - shopId: {}", shopId);

        ReviewListResponse response = reviewService.getStoreReviews(shopId);

        log.info("리뷰 목록 조회 완료 - 리뷰 개수: {}", response.reviews().size());

        return ResponseEntity.ok(response);
    }

    // 랜덤 닉네임 조회
    @GetMapping("/v1/reviews/nickname")
    public ResponseEntity<ReviewNicknameResponse> getRandomNickname() {
        log.info("랜덤 닉네임 조회 요청");

        ReviewNicknameResponse response = reviewService.getRandomNickname();

        log.info("랜덤 닉네임 조회 완료 - nickname: {}", response.nickname());

        return ResponseEntity.ok(response);
    }
}
