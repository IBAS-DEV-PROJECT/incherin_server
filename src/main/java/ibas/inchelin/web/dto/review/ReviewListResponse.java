package ibas.inchelin.web.dto.review;

import java.time.Instant;
import java.util.List;

public record ReviewListResponse (
        List<ReviewResponse> reviews
) {
    public record ReviewResponse(
            Long id,
            String nickname,
            Double rating,
            String content,
            List<String> images,
            Instant createdAt
    ) {}
}
