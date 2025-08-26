package ibas.inchelin.domain.user.service;

import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.UserRepository;
import ibas.inchelin.web.dto.user.MyInfoResponse;
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
        MyInfoResponse dto = new MyInfoResponse();
        dto.setUserId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setName(user.getName());
        dto.setBio(user.getBio());
        dto.setProfileImage(user.getProfileImage());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    public MyInfoResponse updateMyInfo(String nickname, String bio, String sub) {
        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        user.changeInfo(nickname, bio);
        MyInfoResponse dto = new MyInfoResponse();
        dto.setUserId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setName(user.getName());
        dto.setBio(user.getBio());
        dto.setProfileImage(user.getProfileImage());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
