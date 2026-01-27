package ibas.inchelin.web.dto.store;

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
