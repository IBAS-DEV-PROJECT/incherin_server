package ibas.inchelin.auth.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginInfoResponse {
    private final String googleOAuth2Url;
}

