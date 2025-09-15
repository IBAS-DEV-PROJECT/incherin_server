package ibas.inchelin.web.dto.store;

public record StoreInfoResponse(
        String storeName,
        String address,
        String phone,
        Boolean bookmarkedByMe
) {}
