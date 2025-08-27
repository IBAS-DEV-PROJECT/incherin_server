package ibas.inchelin.auth.web.dto;

public record GoogleOAuthTokenResponse(
        String accessToken,
        String refreshToken,
        String email,
        String name,
        String role
) {}
