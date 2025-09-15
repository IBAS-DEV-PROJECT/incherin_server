package ibas.inchelin.web.dto.notice;

import java.util.List;

public record NoticeListResponse(
        List<NoticeResponse> notices
) {
    public record NoticeResponse(
            Long noticeId,
            String title,
            String createdAt
    ) {}
}
