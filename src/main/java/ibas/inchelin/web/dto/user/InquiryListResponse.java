package ibas.inchelin.web.dto.user;

import ibas.inchelin.domain.Status;
import ibas.inchelin.domain.inquiry.InquiryType;

import java.time.LocalDateTime;
import java.util.List;

public record InquiryListResponse(
        List<InquirySummaryResponse> inquiries
) {
    public record InquirySummaryResponse(
            Long inquiryId,
            String title,
            Status status,
            InquiryType type,
            LocalDateTime createdAt
    ) {}
}
