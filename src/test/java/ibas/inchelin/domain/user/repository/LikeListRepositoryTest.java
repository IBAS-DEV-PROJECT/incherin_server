package ibas.inchelin.domain.user.repository;

import ibas.inchelin.domain.user.Role;
import ibas.inchelin.domain.user.entity.LikeList;
import ibas.inchelin.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class LikeListRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LikeListRepository likeListRepository;

    private User user1;
    private User user2;
    private LikeList likeList1;
    private LikeList likeList2;
    private LikeList likeList3;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 사용자들 생성
        user1 = User.builder()
                .email("user1@test.com")
                .name("테스트유저1")
                .role(Role.USER)
                .build();

        user2 = User.builder()
                .email("user2@test.com")
                .name("테스트유저2")
                .role(Role.USER)
                .build();

        // 사용자들을 영속화
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // 좋아요 리스트들 생성 (reflection을 사용하여 private 필드 설정)
        likeList1 = createLikeList("한식 맛집 리스트", user1);
        likeList2 = createLikeList("카페 리스트", user1);
        likeList3 = createLikeList("디저트 맛집 리스트", user2);

        // 좋아요 리스트들을 영속화
        entityManager.persistAndFlush(likeList1);
        entityManager.persistAndFlush(likeList2);
        entityManager.persistAndFlush(likeList3);

        entityManager.clear(); // 영속성 컨텍스트 클리어
    }

    @Test
    @DisplayName("사용자 ID로 좋아요 리스트 조회 - 성공")
    void findByUserId_Success() {
        // When: user1의 좋아요 리스트들을 조회
        List<LikeList> user1LikeLists = likeListRepository.findByUserId(user1.getId());

        // Then: user1의 좋아요 리스트 2개가 조회되어야 함
        assertThat(user1LikeLists).hasSize(2);
        assertThat(user1LikeLists)
                .extracting(LikeList::getName)
                .containsExactlyInAnyOrder("한식 맛집 리스트", "카페 리스트");
    }

    @Test
    @DisplayName("사용자 ID로 좋아요 리스트 조회2 - 성공")
    void findByUserId_DifferentUser() {
        // When: user2의 좋아요 리스트들을 조회
        List<LikeList> user2LikeLists = likeListRepository.findByUserId(user2.getId());

        // Then: user2의 좋아요 리스트 1개가 조회되어야 함
        assertThat(user2LikeLists).hasSize(1);
        assertThat(user2LikeLists.get(0).getName()).isEqualTo("디저트 맛집 리스트");
        assertThat(user2LikeLists.get(0).getUser().getId()).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("좋아요 리스트가 없는 사용자 조회 - 빈 리스트 반환")
    void findByUserId_UserWithoutLikeLists() {
        // Given: 좋아요 리스트가 없는 새로운 사용자 생성
        User userWithoutLists = User.builder()
                .email("empty@test.com")
                .name("빈유저")
                .role(Role.USER)
                .build();
        entityManager.persistAndFlush(userWithoutLists);

        // When: 해당 사용자의 좋아요 리스트 조회
        List<LikeList> likeLists = likeListRepository.findByUserId(userWithoutLists.getId());

        // Then: 빈 리스트가 반환되어야 함
        assertThat(likeLists).isEmpty();
    }

    @Test
    @DisplayName("좋아요 리스트 저장 - 성공")
    void save_LikeList_Success() {
        // Given: 새로운 좋아요 리스트 생성
        LikeList newLikeList = createLikeList("새로운 맛집 리스트", user1);

        // When: 좋아요 리스트 저장
        LikeList savedLikeList = likeListRepository.save(newLikeList);

        // Then: 저장이 성공적으로 되어야 함
        assertThat(savedLikeList).isNotNull();
        assertNotNull(savedLikeList.getId());
        assertThat(savedLikeList.getUser().getId()).isEqualTo(user1.getId());

        // And: 데이터베이스에서 조회했을 때도 존재해야 함
        List<LikeList> user1LikeLists = likeListRepository.findByUserId(user1.getId());
        assertThat(user1LikeLists)
                .extracting(LikeList::getName)
                .containsExactlyInAnyOrder("한식 맛집 리스트", "카페 리스트", "새로운 맛집 리스트");
    }

    @Test
    @DisplayName("좋아요 리스트 삭제 - 성공")
    void delete_LikeList_Success() {
        likeListRepository.delete(likeList1);
        List<LikeList> user1LikeLists = likeListRepository.findByUserId(user1.getId());
        assertThat(user1LikeLists).hasSize(1);
        assertThat(user1LikeLists)
                .extracting(LikeList::getName)
                .containsExactlyInAnyOrder("카페 리스트");
    }

    private LikeList createLikeList(String name, User user) {
            return LikeList.builder()
                    .name(name)
                    .user(user)
                    .build();
    }
}
