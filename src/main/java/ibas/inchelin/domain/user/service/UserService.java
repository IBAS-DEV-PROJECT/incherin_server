package ibas.inchelin.domain.user.service;

import ibas.inchelin.domain.review.repository.*;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.domain.user.entity.Follow;
import ibas.inchelin.domain.user.entity.LikeList;
import ibas.inchelin.domain.user.entity.LikeListStore;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.FollowRepository;
import ibas.inchelin.domain.user.repository.LikeListRepository;
import ibas.inchelin.domain.user.repository.LikeListStoreRepository;
import ibas.inchelin.domain.user.repository.UserRepository;
import ibas.inchelin.web.dto.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final LikeListRepository likeListRepository;
    private final LikeListStoreRepository likeListStoreRepository;
    private final StoreRepository storeRepository;

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
    public MyListResponse getMyLists(String sub) {
        User user = userRepository.findBySub(sub).orElseThrow();
        List<LikeList> likeLists = likeListRepository.findByUserId(user.getId());
        List<MyListResponse.ListNameResponse> myLists = likeLists.stream().map(ll -> new MyListResponse.ListNameResponse(ll.getId(), ll.getName())).toList();
        return new MyListResponse(myLists);
    }

    public void addMyList(String sub, String listName) {
        likeListRepository.save(LikeList.builder()
                .name(listName)
                .user(userRepository.findBySub(sub).orElseThrow())
                .build());
    }

    public void deleteMyList(String sub, Long listId) {
        LikeList likeList = likeListRepository.findById(listId).orElseThrow();
        User user = userRepository.findBySub(sub).orElseThrow();
        if (!likeList.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 리스트만 삭제할 수 있습니다.");
        }
        likeListRepository.delete(likeList);
    }

    @Transactional(readOnly = true)
    public MyListItemListResponse getMyListItems(String sub, Long listId) {
        LikeList likeList = likeListRepository.findById(listId).orElseThrow();
        User user = userRepository.findBySub(sub).orElseThrow();
        if (!likeList.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 리스트에서만 조회할 수 있습니다.");
        }
        List<LikeListStore> likeListStores = likeListStoreRepository.findByLikeListId(listId);
        List<MyListItemListResponse.MyListItemResponse> stores = likeListStores.stream()
                .map(lls -> new MyListItemListResponse.MyListItemResponse(
                        lls.getId(),
                        lls.getStore().getId(),
                        lls.getStore().getStoreName()
                )).toList();
        return new MyListItemListResponse(stores);
    }

    public void addMyListItem(String sub, Long listId, Long storeId) {
        User user = userRepository.findBySub(sub).orElseThrow();
        LikeList likeList = likeListRepository.findById(listId).orElseThrow();
        if (!likeList.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 리스트에만 추가할 수 있습니다.");
        }
        Store store = storeRepository.findById(storeId).orElseThrow();
        likeListStoreRepository.save(LikeListStore.builder()
                .likeList(likeList)
                .store(store)
                .build());
    }

    public void deleteMyListItem(String sub, Long itemId) {
        LikeListStore likeStore = likeListStoreRepository.findById(itemId).orElseThrow();
        User user = userRepository.findBySub(sub).orElseThrow();
        if(!likeStore.getLikeList().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 리스트에서만 삭제할 수 있습니다.");
        }
        likeListStoreRepository.delete(likeStore);
    }
}
