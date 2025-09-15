package ibas.inchelin.web.dto.store;

public record BusinessHourListResponse(
        BusinessHourResponse mon,
        BusinessHourResponse tue,
        BusinessHourResponse wed,
        BusinessHourResponse thu,
        BusinessHourResponse fri,
        BusinessHourResponse sat,
        BusinessHourResponse sun
) {
    public record BusinessHourResponse(
            String open,
            String close,
            Boolean isBusinessDay
    ) {}
}
