package ibas.inchelin.web.dto.review;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewListResponse (
        List<ReviewResponse> reviews
) {
    public record ReviewResponse(
            Long id,
            String nickname,
            int rating,
            String content,
            List<String> images,
            LocalDateTime createdAt
    ) {}
}
