package ibas.inchelin.web.dto.user;

import lombok.Data;

@Data
public class OtherUserInfoResponse {
    private String nickname;
    private String name;
    private String bio;
    private String profileImage;
}