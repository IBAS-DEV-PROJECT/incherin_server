package ibas.inchelin.web.dto.user;

import java.util.List;

public record FollowerListResponse(
        int totalCount,
        List<UserNicknameResponse> followers
) {}