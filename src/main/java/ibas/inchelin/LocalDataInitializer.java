package ibas.inchelin;

import ibas.inchelin.domain.review.Keyword;
import ibas.inchelin.domain.review.entity.Review;
import ibas.inchelin.domain.review.entity.ReviewKeyword;
import ibas.inchelin.domain.review.entity.ReviewMenu;
import ibas.inchelin.domain.review.entity.ReviewPhoto;
import ibas.inchelin.domain.review.repository.ReviewKeywordRepository;
import ibas.inchelin.domain.review.repository.ReviewMenuRepository;
import ibas.inchelin.domain.review.repository.ReviewPhotoRepository;
import ibas.inchelin.domain.review.repository.ReviewRepository;
import ibas.inchelin.domain.store.entity.Menu;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.store.repository.MenuRepository;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.domain.user.Role;
import ibas.inchelin.domain.user.entity.Follow;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.FollowRepository;
import ibas.inchelin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewMenuRepository reviewMenuRepository;
    private final MenuRepository menuRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final UserRepository UserRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initUser("test1@example.com", "테스터1");
        initUser("test2@example.com", "테스터2");
        initUser("test3@example.com", "테스터3");
        initUser("test4@example.com", "테스터4");
        initFollow(2L, 1L);
        initFollow(3L, 1L);
        initFollow(1L, 4L);
        initStore();
        initReview();
        initReviewMenu();
        initReviewPhoto();
        initReviewKeyword();
    }

    private void initUser(String email, String name) {
        if (userRepository.findByEmail(email).isEmpty()) {
            userRepository.save(User.builder()
                    .email(email)
                    .name(name)
                    .role(Role.USER)
                    .build());
        }
    }

    private void initFollow(Long userId, Long FollowUserID) {
        followRepository.save(Follow.builder()
                .user(userRepository.findById(userId).orElseThrow())
                .followUser(userRepository.findById(FollowUserID).orElseThrow())
                .build());
    }

    private void initStore() {
        storeRepository.save(Store.builder().storeName("store1").build());
    }

    private void initReview() {
        reviewRepository.save(Review.builder()
                .rating(4.0)
                .content("리뷰1")
                .store(storeRepository.findById(1L).orElseThrow())
                .writtenBy(userRepository.findById(1L).orElseThrow())
                .build());
    }

    private void initReviewMenu() {
        Menu menu = Menu.builder().name("메뉴1").price(1000).build();
        menuRepository.save(menu);
        reviewMenuRepository.save(ReviewMenu.builder()
                .review(reviewRepository.findById(1L).orElseThrow())
                .menu(menu).build());
    }

    private void initReviewPhoto() {
        reviewPhotoRepository.save(ReviewPhoto.builder()
                .imageUrl("https://example.com/photo1.jpg")
                .review(reviewRepository.findById(1L).orElseThrow()).build());
    }

    private void initReviewKeyword() {
        reviewKeywordRepository.save(ReviewKeyword.builder()
                .keyword(Keyword.DELICIOUS)
                .review(reviewRepository.findById(1L).orElseThrow()).build());
    }
}
