package ibas.inchelin.auth.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutRequest {
    // 없으면 현재 인증 정보 기반 처리
    private String refreshToken;
}

