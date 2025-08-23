package ibas.inchelin.web.dto.user;

import ibas.inchelin.domain.user.Role;
import lombok.Data;

@Data
public class MyInfoResponse {
    private Long userId;
    private String nickname;
    private String name;
    private String bio;
    private String profileImage;
    private String email;
    private Role role;
}
