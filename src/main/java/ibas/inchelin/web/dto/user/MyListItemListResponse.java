package ibas.inchelin.web.dto.user;

import java.util.List;

public record MyListItemListResponse(
        List<MyListItemResponse> items
) {
    public record MyListItemResponse(
            Long itemId,
            Long storeId,
            String storeName
    ) {}
}
