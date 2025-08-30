package ibas.inchelin.web.dto.review;

import lombok.Data;

import java.util.List;

//@Data
//public class ReviewListResponse {
//    private List<ReviewResponse> reviews;
//}

public record ReviewListResponse (
        List<ReviewResponse> reviews
) {}
