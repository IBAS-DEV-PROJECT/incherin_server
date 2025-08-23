package ibas.inchelin.web.dto.store;

import lombok.Data;

@Data
public class StoreInfoResponse {
    private String storeName;
    private String address;
    private String phone;
    private Boolean bookmarkedByMe;
}
