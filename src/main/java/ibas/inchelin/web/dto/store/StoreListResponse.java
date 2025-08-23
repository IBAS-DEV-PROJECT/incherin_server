package ibas.inchelin.web.dto.store;

import ibas.inchelin.domain.store.Category;
import lombok.Data;

import java.util.List;

@Data
public class StoreListResponse {
    private List<StoreListItemResponse> stores;

    @Data
    public static class StoreListItemResponse {
        private Long storeId;
        private String storeName;
        private Category category;
        private String thumbnail;
        private Boolean isOpen;
        private Boolean isDeliveryAvailable;
        private int distance;
        private Double rating;
        private int reviewCount;
        private int bookmarkCount;
    }
}
