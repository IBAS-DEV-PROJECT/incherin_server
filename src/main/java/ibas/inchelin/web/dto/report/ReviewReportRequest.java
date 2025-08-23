package ibas.inchelin.web.dto.report;

import ibas.inchelin.domain.report.ReportType;
import lombok.Data;

@Data
public class ReviewReportRequest {
    private ReportType type;
    private String content;
}
