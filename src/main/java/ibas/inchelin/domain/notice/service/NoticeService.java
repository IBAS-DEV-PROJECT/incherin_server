package ibas.inchelin.domain.notice.service;

import ibas.inchelin.domain.notice.entity.Notice;
import ibas.inchelin.domain.notice.repository.NoticeRepository;
import ibas.inchelin.web.dto.notice.NoticeContentResponse;
import ibas.inchelin.web.dto.notice.NoticeListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지 목록 조회
    public NoticeListResponse findNotices() {
        List<Notice> notices = noticeRepository.findAll(); //DB에서 모든 공지를 조회
        List<NoticeListResponse.NoticeResponse> noticeResponses = notices.stream()
                .map(n -> new NoticeListResponse.NoticeResponse(
                        n.getId(),
                        n.getTitle(),
                        n.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                )).toList();
        return new NoticeListResponse(noticeResponses);

    }

    // 공지 내용 조회
    public NoticeContentResponse findNoticeContent(Long noticeId) {
        // DB에서 noticeId에 해당하는 공지를 조회. 없으면 예외 발생
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지를 찾을 수 없습니다."));

        return new NoticeContentResponse(notice.getContent());
    }
}
