package ibas.inchelin.web.dto.review;

import java.util.List;

public record ReviewListResponse (
        List<ReviewResponse> reviews
) {}
