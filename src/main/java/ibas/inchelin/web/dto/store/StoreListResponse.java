package ibas.inchelin.web.dto.store;

import ibas.inchelin.domain.store.Category;

import java.util.List;

public record StoreListResponse(
        List<StoreListItemResponse> stores
) {
    public record StoreListItemResponse(
            Long id,
            String name,
            String category,
            String thumbnail
    ) {}
}
