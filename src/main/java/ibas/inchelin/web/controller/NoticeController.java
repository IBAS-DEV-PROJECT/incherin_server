package ibas.inchelin.web.controller;

import ibas.inchelin.domain.notice.service.NoticeService;
import ibas.inchelin.web.dto.notice.NoticeContentResponse;
import ibas.inchelin.web.dto.notice.NoticeListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController //반환값 JSON
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지 목록 조회 API
    @GetMapping // HTTP GET 요청과 'notices' url을 이 메서드와 매핑함
    public ResponseEntity<NoticeListResponse> getNotices() {
        return ResponseEntity.ok(noticeService.findNotices()); // 200 OK 상태 코드와 함께 조회된 목록을 응답 body에 담아 반환
    }

    // 공지 내용 조회 API
    @GetMapping("/{noticeId}") // HTTP GET 요청과 '/notices/{noticeId}' url을 이 메서드와 매핑합니다.
    public ResponseEntity<NoticeContentResponse> getNotice(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.findNoticeContent(noticeId));
    }
}
