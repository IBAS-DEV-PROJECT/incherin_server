package ibas.inchelin.web.dto.inquiry;

import ibas.inchelin.domain.inquiry.InquiryType;
import lombok.Data;

@Data
public class InquiryRequest {
    private String title;
    private String content;
    private InquiryType type;
}
