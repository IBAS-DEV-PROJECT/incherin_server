package ibas.inchelin.web.dto.store;

public record StoreSummaryResponse(
        Double rating,
        int reviewCount,
        int bookmarkCount,
        String aiSummary
) {
}
