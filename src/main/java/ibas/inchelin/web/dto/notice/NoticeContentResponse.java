package ibas.inchelin.web.dto.notice;

import ibas.inchelin.domain.notice.entity.Notice;
import lombok.Getter;

public class NoticeContentResponse {

    //공지 내용 조회를 위한 dto
    @Getter
    public static class NoticeDetailDto {
        private String content;

        public NoticeDetailDto(Notice notice) {
            this.content = notice.getContent();
        }
    }
}