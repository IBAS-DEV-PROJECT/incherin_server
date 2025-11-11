package ibas.inchelin.web.dto.user;

public record OtherUserInfoResponse(
        Long userId,
        String nickname,
        String bio,
        String profileImage,
        int followers,
        int following
) {}