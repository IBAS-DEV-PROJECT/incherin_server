package ibas.inchelin.web.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class FollowingListResponse {
    private int totalCount;
    private List<UserNicknameResponse> followings;
}
