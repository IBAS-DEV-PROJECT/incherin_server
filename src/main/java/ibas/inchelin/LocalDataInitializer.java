package ibas.inchelin;

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
}
