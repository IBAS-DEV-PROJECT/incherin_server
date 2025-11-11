package ibas.inchelin.web.dto.user;

public record MyInfoResponse(
        Long userId,
        String nickname,
        String name,
        String bio,
        String profileImage,
        String email,
        int followers,
        int following
) {}
