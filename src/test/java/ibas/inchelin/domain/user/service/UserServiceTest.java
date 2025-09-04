package ibas.inchelin.domain.user.service;

import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.user.Role;
import ibas.inchelin.domain.user.entity.LikeList;
import ibas.inchelin.domain.user.entity.LikeListStore;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.*;
import ibas.inchelin.domain.review.repository.*;
import ibas.inchelin.web.dto.user.MyListItemListResponse;
import ibas.inchelin.web.dto.user.MyListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LikeListRepository likeListRepository;
    @Mock
    private LikeListStoreRepository likeListStoreRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private final String sub = "sub-123";

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .name("Tester")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "sub", sub);
    }

    @Test
    @DisplayName("리스트 조회 - 성공")
    void getMyLists_success() {
        // given
        LikeList list1 = mock(LikeList.class);
        LikeList list2 = mock(LikeList.class);
        given(list1.getId()).willReturn(10L);
        given(list1.getName()).willReturn("한식 맛집 리스트");
        given(list2.getId()).willReturn(11L);
        given(list2.getName()).willReturn("디저트 맛집 리스트");

        given(userRepository.findBySub(sub)).willReturn(Optional.of(user));
        given(likeListRepository.findByUserId(1L)).willReturn(List.of(list1, list2));

        // when
        MyListResponse response = userService.getMyLists(sub);

        // then
        assertThat(response.lists()).hasSize(2);
        assertThat(response.lists().get(0).listId()).isEqualTo(10L);
        assertThat(response.lists().get(0).listName()).isEqualTo("한식 맛집 리스트");
        assertThat(response.lists().get(1).listId()).isEqualTo(11L);
        assertThat(response.lists().get(1).listName()).isEqualTo("디저트 맛집 리스트");
    }

    @Test
    @DisplayName("리스트 추가 - 성공")
    void addMyLists_success() {
        // given
        String listName = "새로운 맛집 리스트";
        given(userRepository.findBySub(sub)).willReturn(Optional.of(user));

        // when
        userService.addMyList(sub, listName);

        // then
        verify(likeListRepository).save(any(LikeList.class));
    }

    @Test
    @DisplayName("리스트 삭제 - 성공")
    void deleteMyLists_success() {
        // given
        Long listId = 10L;

        LikeList likeList = mock(LikeList.class);
        given(likeList.getUser()).willReturn(user);

        given(userRepository.findBySub(sub)).willReturn(Optional.of(user));
        given(likeListRepository.findById(listId)).willReturn(Optional.of(likeList));

        // when
        userService.deleteMyList(sub, listId);

        // then
        verify(likeListRepository).delete(likeList);
    }

    @Test
    @DisplayName("리스트에 가게 조회 - 성공")
    void getMyListItems_success() {
        // given
        Long listId = 10L;

        Store store1 = mock(Store.class);
        Store store2 = mock(Store.class);
        given(store1.getId()).willReturn(100L);
        given(store1.getStoreName()).willReturn("맛집1");
        given(store2.getId()).willReturn(101L);
        given(store2.getStoreName()).willReturn("맛집2");

        LikeListStore likeListStore1 = mock(LikeListStore.class);
        LikeListStore likeListStore2 = mock(LikeListStore.class);
        given(likeListStore1.getId()).willReturn(1L);
        given(likeListStore1.getStore()).willReturn(store1);
        given(likeListStore2.getId()).willReturn(2L);
        given(likeListStore2.getStore()).willReturn(store2);

        LikeList likeList = mock(LikeList.class);
        given(likeList.getUser()).willReturn(user);

        given(likeListRepository.findById(listId)).willReturn(Optional.of(likeList));
        given(userRepository.findBySub(sub)).willReturn(Optional.of(user));
        given(likeListStoreRepository.findByLikeListId(listId)).willReturn(List.of(likeListStore1, likeListStore2));

        // when
        MyListItemListResponse response = userService.getMyListItems(sub, listId);

        // then
        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).itemId()).isEqualTo(1L);
        assertThat(response.items().get(0).storeId()).isEqualTo(100L);
        assertThat(response.items().get(0).storeName()).isEqualTo("맛집1");
        assertThat(response.items().get(1).itemId()).isEqualTo(2L);
        assertThat(response.items().get(1).storeId()).isEqualTo(101L);
        assertThat(response.items().get(1).storeName()).isEqualTo("맛집2");
    }
}
