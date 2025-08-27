package ibas.inchelin.web.dto.user;

public record OtherUserInfoResponse(
        String nickname,
        String name,
        String bio,
        String profileImage
) {}