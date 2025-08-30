package ibas.inchelin.domain.user.service;

import ibas.inchelin.domain.user.Role;
import ibas.inchelin.domain.user.entity.LikeList;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.*;
import ibas.inchelin.domain.review.repository.*;
import ibas.inchelin.web.dto.user.MyListResponse;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private LikeListRepository likeListRepository;

    @InjectMocks private UserService userService;

    @Test
    @DisplayName("getMyLists 성공 케이스")
    void getMyLists_success() {
        // given
        String sub = "sub-123";
        User user = User.builder()
                .email("test@example.com")
                .name("Tester")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "sub", sub);

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
}

