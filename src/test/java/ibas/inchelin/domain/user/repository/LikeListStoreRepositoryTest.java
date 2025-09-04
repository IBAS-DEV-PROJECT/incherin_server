package ibas.inchelin.domain.user.repository;

import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.user.Role;
import ibas.inchelin.domain.user.entity.LikeList;
import ibas.inchelin.domain.user.entity.LikeListStore;
import ibas.inchelin.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LikeListStoreRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LikeListStoreRepository likeListStoreRepository;

    private LikeList likeList;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("user1@test.com")
                .name("테스트유저1")
                .role(Role.USER)
                .build();
        entityManager.persistAndFlush(user);

        likeList = LikeList.builder()
                .name("한식 맛집 리스트")
                .user(user)
                .build();
        entityManager.persistAndFlush(likeList);

        Store store1 = Store.builder()
                .storeName("식당1")
                .build();
        Store store2 = Store.builder()
                .storeName("식당2")
                .build();
        entityManager.persistAndFlush(store1);
        entityManager.persistAndFlush(store2);

        LikeListStore likeListStore1 = LikeListStore.builder()
                .likeList(likeList)
                .store(store1)
                .build();
        LikeListStore likeListStore2 = LikeListStore.builder()
                .likeList(likeList)
                .store(store2)
                .build();
        entityManager.persistAndFlush(likeListStore1);
        entityManager.persistAndFlush(likeListStore2);

        entityManager.clear();
    }

    @Test
    @DisplayName("리스트에 추가한 가게 조회 - 성공")
    void findByLikeListId_success() {
        List<LikeListStore> likeListStores = likeListStoreRepository.findByLikeListId(likeList.getId());

        assertThat(likeListStores).hasSize(2);
        assertThat(likeListStores)
                .extracting(LikeListStore::getStore)
                .extracting(Store::getStoreName)
                .containsExactlyInAnyOrder("식당1", "식당2");
    }
}