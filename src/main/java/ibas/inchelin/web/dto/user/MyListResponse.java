package ibas.inchelin.web.dto.user;

import java.util.List;

public record MyListResponse(
        List<ListNameResponse> lists
) {
    public record ListNameResponse(
            Long listId,
            String listName
    ) {}
}
