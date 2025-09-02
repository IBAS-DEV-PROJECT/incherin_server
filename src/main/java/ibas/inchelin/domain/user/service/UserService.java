package ibas.inchelin.domain.user.service;

import ibas.inchelin.domain.review.entity.Review;
import ibas.inchelin.domain.review.entity.ReviewPhoto;
import ibas.inchelin.domain.review.repository.*;
import ibas.inchelin.domain.user.entity.Follow;
import ibas.inchelin.domain.user.entity.LikeList;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.FollowRepository;
import ibas.inchelin.domain.user.repository.LikeListRepository;
import ibas.inchelin.domain.user.repository.UserRepository;
import ibas.inchelin.web.dto.review.ReviewListResponse;
import ibas.inchelin.web.dto.review.ReviewResponse;
import ibas.inchelin.web.dto.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final ReviewMenuRepository reviewMenuRepository;
    private final LikeListRepository likeListRepository;

    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo(String sub) {
        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        return new MyInfoResponse(user.getId(), user.getNickname(), user.getName(), user.getBio(), user.getProfileImage(), user.getEmail(), user.getRole());
    }

    public MyInfoResponse updateMyInfo(String nickname, String bio, String sub) {
        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        user.changeInfo(nickname, bio);
        return new MyInfoResponse(user.getId(), user.getNickname(), user.getName(), user.getBio(), user.getProfileImage(), user.getEmail(), user.getRole());
    }

    @Transactional(readOnly = true)
    public OtherUserInfoResponse otherUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        return new OtherUserInfoResponse(user.getNickname(), user.getName(), user.getBio(), user.getProfileImage());
    }

    @Transactional(readOnly = true)
    public FollowerListResponse getFollowers(Long userId) {
        List<Follow> followList = followRepository.findByFollowUserId(userId);
        List<UserNicknameResponse> followers = followList.stream()
                .map(follow -> new UserNicknameResponse(follow.getUser().getId(), follow.getUser().getNickname()))
                .toList();
        return new FollowerListResponse(followers.size(), followers);
    }

    @Transactional(readOnly = true)
    public FollowingListResponse getFollowing(Long userId) {
        List<Follow> followList = followRepository.findByUserId(userId);
        List<UserNicknameResponse> followings = followList.stream()
                .map(follow -> new UserNicknameResponse(follow.getFollowUser().getId(), follow.getFollowUser().getNickname()))
                .toList();
        return new FollowingListResponse(followings.size(), followings);
    }

    public void follow(String sub, Long targetUserId) {
        followRepository.save(Follow.builder()
                .user(userRepository.findBySub(sub).orElseThrow())
                .followUser(userRepository.findById(targetUserId).orElseThrow())
                .build());
    }

    public void unfollow(String sub, Long targetUserId) {
        Follow follow = followRepository.findByUserIdAndFollowUserId(
                userRepository.findBySub(sub).orElseThrow().getId(),
                targetUserId
        );
        followRepository.delete(follow);
    }

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

    @Transactional(readOnly = true)
    public MyListResponse getMyLists(String sub) {
        User user = userRepository.findBySub(sub).orElseThrow();
        List<LikeList> likeLists = likeListRepository.findByUserId(user.getId());
        List<MyListResponse.ListNameResponse> myLists = likeLists.stream().map(ll -> new MyListResponse.ListNameResponse(ll.getId(), ll.getName())).toList();
        return new MyListResponse(myLists);
    }

    public void addMyLists(String sub, String listName) {
        likeListRepository.save(LikeList.builder()
                .name(listName)
                .user(userRepository.findBySub(sub).orElseThrow())
                .build());
    }
}
