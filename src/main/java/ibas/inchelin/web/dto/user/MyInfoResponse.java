package ibas.inchelin.web.dto.user;

import ibas.inchelin.domain.user.Role;

public record MyInfoResponse(
        Long userId,
        String nickname,
        String name,
        String bio,
        String profileImage,
        String email,
        Role role
) {}
