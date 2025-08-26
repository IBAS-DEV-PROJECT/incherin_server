package ibas.inchelin.auth.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleOAuthTokenRequest {
    private String code;
}

