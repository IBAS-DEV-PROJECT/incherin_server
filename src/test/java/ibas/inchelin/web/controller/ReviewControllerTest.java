package ibas.inchelin.web.controller;

import ibas.inchelin.domain.S3Service;
import ibas.inchelin.domain.review.service.ReviewService;
import ibas.inchelin.web.dto.review.ReviewListResponse;
import ibas.inchelin.web.dto.review.ReviewNicknameResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = ReviewController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ReviewService reviewService;

    @MockitoBean
    S3Service s3Service;

    // ───────────────────────────────────────────────
    // POST /v1/shops/{shopId}/reviews
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("리뷰 등록 - 이미지 없이 성공")
    void writeReview_withoutImages() throws Exception {
        ReviewListResponse.ReviewResponse created = new ReviewListResponse.ReviewResponse(
                1L, "빠른 수학자", 4, "맛있어요", List.of(), LocalDateTime.of(2026, 4, 3, 12, 0));

        given(reviewService.write(eq(10L), eq("빠른 수학자"), eq(4.0), eq("맛있어요"), isNull()))
                .willReturn(created);

        mockMvc.perform(multipart("/v1/shops/10/reviews")
                        .param("nickname", "빠른 수학자")
                        .param("rating", "4.0")
                        .param("content", "맛있어요")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/shops/10/reviews/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nickname").value("빠른 수학자"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.content").value("맛있어요"));

        verify(reviewService).write(10L, "빠른 수학자", 4.0, "맛있어요", null);
    }

    @Test
    @DisplayName("리뷰 등록 - 이미지 포함 성공")
    void writeReview_withImages() throws Exception {
        List<String> uploadedUrls = List.of("https://cdn.example.com/img1.jpg", "https://cdn.example.com/img2.jpg");

        ReviewListResponse.ReviewResponse created = new ReviewListResponse.ReviewResponse(
                2L, "느린 물리학자", 5, "최고예요", uploadedUrls, LocalDateTime.of(2026, 4, 3, 13, 0));

        given(s3Service.uploadMany(anyList())).willReturn(uploadedUrls);
        given(reviewService.write(eq(10L), eq("느린 물리학자"), eq(5.0), eq("최고예요"), eq(uploadedUrls)))
                .willReturn(created);

        MockMultipartFile image1 = new MockMultipartFile("images", "img1.jpg", MediaType.IMAGE_JPEG_VALUE, "data1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "img2.jpg", MediaType.IMAGE_JPEG_VALUE, "data2".getBytes());

        mockMvc.perform(multipart("/v1/shops/10/reviews")
                        .file(image1)
                        .file(image2)
                        .param("nickname", "느린 물리학자")
                        .param("rating", "5.0")
                        .param("content", "최고예요")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/shops/10/reviews/2"))
                .andExpect(jsonPath("$.images[0]").value("https://cdn.example.com/img1.jpg"))
                .andExpect(jsonPath("$.images[1]").value("https://cdn.example.com/img2.jpg"));

        verify(s3Service).uploadMany(anyList());
        verify(reviewService).write(10L, "느린 물리학자", 5.0, "최고예요", uploadedUrls);
    }

    // ───────────────────────────────────────────────
    // GET /v1/shops/{shopId}/reviews
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("리뷰 목록 조회 - 성공")
    void getStoreReviews() throws Exception {
        ReviewListResponse.ReviewResponse review = new ReviewListResponse.ReviewResponse(
                1L, "빠른 수학자", 4, "맛있어요", List.of(), LocalDateTime.of(2026, 4, 3, 12, 0));
        ReviewListResponse response = new ReviewListResponse(List.of(review));

        given(reviewService.getStoreReviews(10L)).willReturn(response);

        mockMvc.perform(get("/v1/shops/10/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews").isArray())
                .andExpect(jsonPath("$.reviews.length()").value(1))
                .andExpect(jsonPath("$.reviews[0].id").value(1))
                .andExpect(jsonPath("$.reviews[0].nickname").value("빠른 수학자"))
                .andExpect(jsonPath("$.reviews[0].rating").value(4));

        verify(reviewService).getStoreReviews(10L);
    }

    @Test
    @DisplayName("리뷰 목록 조회 - 리뷰 없는 경우 빈 배열 반환")
    void getStoreReviews_empty() throws Exception {
        given(reviewService.getStoreReviews(99L)).willReturn(new ReviewListResponse(List.of()));

        mockMvc.perform(get("/v1/shops/99/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews").isArray())
                .andExpect(jsonPath("$.reviews.length()").value(0));
    }

    // ───────────────────────────────────────────────
    // GET /v1/reviews/nickname
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("랜덤 닉네임 조회 - 성공")
    void getRandomNickname() throws Exception {
        given(reviewService.getRandomNickname()).willReturn(new ReviewNicknameResponse("빠른 수학자"));

        mockMvc.perform(get("/v1/reviews/nickname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("빠른 수학자"));

        verify(reviewService).getRandomNickname();
    }
}
