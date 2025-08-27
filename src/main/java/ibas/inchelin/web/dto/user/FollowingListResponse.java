package ibas.inchelin.web.dto.user;

import java.util.List;

public record FollowingListResponse(
        int totalCount,
        List<UserNicknameResponse> followings
) {}
