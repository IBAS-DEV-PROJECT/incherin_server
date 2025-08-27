package ibas.inchelin.domain.user.service;

import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.UserRepository;
import ibas.inchelin.web.dto.user.MyInfoResponse;
import ibas.inchelin.web.dto.user.OtherUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

    public OtherUserInfoResponse otherUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        return new OtherUserInfoResponse(user.getNickname(), user.getName(), user.getBio(), user.getProfileImage());
    }
}
