package ibas.inchelin.web.dto.store;

import lombok.Data;

@Data
public class BusinessHourListResponse {
    private String mon;
    private String tue;
    private String wed;
    private String thu;
    private String fri;
    private String sat;
    private String sun;

    @Data
    public static class BusinessHourResponse {
        private String open;
        private String close;
        private Boolean isBusinessDay;
    }
}
