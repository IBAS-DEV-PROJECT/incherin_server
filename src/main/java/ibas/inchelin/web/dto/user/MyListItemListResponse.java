package ibas.inchelin.web.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class MyListItemListResponse {
    private List<MyListItemResponse> items;

    @Data
    public static class MyListItemResponse {
        private Long itemId;
        private Long storeId;
        private String storeName;
    }
}
