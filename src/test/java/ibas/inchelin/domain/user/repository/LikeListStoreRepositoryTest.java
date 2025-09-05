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
    private LikeListStore likeListStore1;
    private LikeListStore likeListStore2;

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

        likeListStore1 = LikeListStore.builder()
                .likeList(likeList)
                .store(store1)
                .build();
        likeListStore2 = LikeListStore.builder()
                .likeList(likeList)
                .store(store2)
                .build();
        entityManager.persistAndFlush(likeListStore1);
        entityManager.persistAndFlush(likeListStore2);

        entityManager.clear();
    }

    @Test
    @DisplayName("리스트 아이디로 리스트에 추가한 가게 조회 - 성공")
    void findByLikeListId_success() {
        List<LikeListStore> likeListStores = likeListStoreRepository.findByLikeListId(likeList.getId());

        assertThat(likeListStores)
                .extracting(LikeListStore::getStore)
                .extracting(Store::getStoreName)
                .containsExactlyInAnyOrder("식당1", "식당2");
    }

    @Test
    @DisplayName("리스트에 가게 추가 - 성공")
    void save_success() {
        Store store3 = Store.builder()
                .storeName("식당3")
                .build();
        entityManager.persistAndFlush(store3);

        LikeListStore likeListStore3 = LikeListStore.builder()
                .likeList(likeList)
                .store(store3)
                .build();

        LikeListStore savedLikeListStore = likeListStoreRepository.save(likeListStore3);

        assertThat(savedLikeListStore).isNotNull();
        assertNotNull(savedLikeListStore.getId());

        List<LikeListStore> likeListStores = likeListStoreRepository.findByLikeListId(likeList.getId());
        assertThat(likeListStores).extracting(LikeListStore::getStore).extracting(Store::getStoreName)
                .containsExactlyInAnyOrder("식당1", "식당2", "식당3");
    }

    @Test
    @DisplayName("리스트에서 가게 삭제 - 성공")
    void delete_success() {
        likeListStoreRepository.delete(likeListStore1);
        List<LikeListStore> likeListStores = likeListStoreRepository.findByLikeListId(likeList.getId());
        assertThat(likeListStores)
                .extracting(LikeListStore::getStore)
                .extracting(Store::getStoreName)
                .containsExactly("식당2");
    }
}