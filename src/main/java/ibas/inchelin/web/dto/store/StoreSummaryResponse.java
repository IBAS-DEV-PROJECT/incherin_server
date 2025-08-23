package ibas.inchelin.web.dto.store;

import lombok.Data;

@Data
public class StoreSummaryResponse {
    private Double rating;
    private int reviewCount;
    private int bookmarkCount;
    private String aiSummary;
}
