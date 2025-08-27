package ibas.inchelin.domain.user.service;

import ibas.inchelin.domain.user.entity.Follow;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.FollowRepository;
import ibas.inchelin.domain.user.repository.UserRepository;
import ibas.inchelin.web.dto.user.FollowerListResponse;
import ibas.inchelin.web.dto.user.MyInfoResponse;
import ibas.inchelin.web.dto.user.OtherUserInfoResponse;
import ibas.inchelin.web.dto.user.UserNicknameResponse;
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
}
