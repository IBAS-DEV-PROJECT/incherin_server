package ibas.inchelin;

import ibas.inchelin.domain.review.entity.*;
import ibas.inchelin.domain.review.repository.*;
import ibas.inchelin.domain.store.repository.MenuRepository;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 로컬 프로필에서만 활성화되는 특정 API 후처리 테스트용 훅.
 * 목표: 지정된 API 호출이 정상 완료된 후 1회(or 매회) 커스텀 로직 수행.
 */
@Configuration
@Profile("local")
@RequiredArgsConstructor
public class LocalApiHookConfig implements WebMvcConfigurer {

    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final ReviewMenuRepository reviewMenuRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Value("${app.test-hook.target-path:/api/auth/oauth2/google/token}")
    private String targetPathPattern; // Ant 패턴 사용 가능

    @Value("${app.test-hook.run-once:true}")
    private boolean runOnce;

    private final AtomicBoolean executed = new AtomicBoolean(false);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AfterCallTestInterceptor());
    }

    private class AfterCallTestInterceptor implements HandlerInterceptor {
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            // 성공 응답(2xx) 만 대상
            if (response.getStatus() / 100 != 2) return;
            String uri = request.getRequestURI();
            if (!pathMatcher.match(targetPathPattern, uri)) return;
            if (runOnce && executed.get()) return;
            if (runOnce && !executed.compareAndSet(false, true)) return;
            runTestHook();
        }
    }

    private void runTestHook() {
        initReview();
        initReviewLike();
    }

    private void initReview() {
        reviewRepository.save(Review.builder()
                .rating(4.5)
                .content("내 리뷰1")
                .store(storeRepository.findById(1L).orElseThrow())
                .writtenBy("싱그러운 체육학과")
                .build());
    }

    private void initReviewLike() {
        reviewLikeRepository.save(ReviewLike.builder()
                .review(reviewRepository.findById(2L).orElseThrow())
                .user(userRepository.findById(1L).orElseThrow())
                .build());
    }
}

