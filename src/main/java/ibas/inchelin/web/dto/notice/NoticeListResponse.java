package ibas.inchelin.web.dto.notice;

import ibas.inchelin.domain.notice.entity.Notice;
import lombok.Getter;

public class NoticeListResponse {

    //공지 목록 조회를 위한 dto
    @Getter
    public static class NoticeListDto {
        private Long noticeId;
        private String title;

        public NoticeListDto(Notice notice) {
            this.noticeId = notice.getId();
            this.title = notice.getTitle();
        }
    }
}