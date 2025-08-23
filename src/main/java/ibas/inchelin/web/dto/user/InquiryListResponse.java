package ibas.inchelin.web.dto.user;

import ibas.inchelin.domain.Status;
import ibas.inchelin.domain.inquiry.InquiryType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InquiryListResponse {
    private List<InquirySummaryResponse> inquiries;

    @Data
    public static class InquirySummaryResponse {
        private Long inquiryId;
        private String title;
        private Status status;
        private InquiryType type;
        private LocalDateTime createdAt;
    }
}
