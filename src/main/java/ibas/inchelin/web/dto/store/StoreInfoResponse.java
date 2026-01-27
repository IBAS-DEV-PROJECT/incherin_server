package ibas.inchelin.web.dto.store;

public record StoreInfoResponse(
        Long id,
        String name,
        String category,
        String tel,
        String address
) {}
