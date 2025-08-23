package ibas.inchelin.web.dto.notice;

import lombok.Data;

import java.util.List;

@Data
public class NoticeListResponse {
    private List<NoticeResponse> notices;

    @Data
    public static class NoticeResponse {
        private Long noticeId;
        private String title;
        private String createdAt;
    }
}
