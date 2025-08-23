package ibas.inchelin.web.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class MyListResponse {
    private List<ListNameResponse> lists;

    @Data
    public static class ListNameResponse {
        private Long listId;
        private String listName;
    }
}
