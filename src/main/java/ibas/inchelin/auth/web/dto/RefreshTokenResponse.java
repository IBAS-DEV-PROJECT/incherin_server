package ibas.inchelin.auth.web.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}

