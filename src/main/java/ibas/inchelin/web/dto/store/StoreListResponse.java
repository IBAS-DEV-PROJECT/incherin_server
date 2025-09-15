package ibas.inchelin.web.dto.store;

import ibas.inchelin.domain.store.Category;

import java.util.List;

public record StoreListResponse(
        List<StoreListItemResponse> stores
) {
    public record StoreListItemResponse(
            Long storeId,
            String storeName,
            Category category,
            String thumbnail,
            Boolean isOpen,
            Boolean isDeliveryAvailable,
            int distance,
            Double rating,
            int reviewCount,
            int bookmarkCount
    ) {}
}
