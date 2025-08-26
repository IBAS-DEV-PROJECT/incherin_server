package ibas.inchelin.auth.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GoogleOAuthTokenResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String email;
    private final String name;
    private final String role;
}

