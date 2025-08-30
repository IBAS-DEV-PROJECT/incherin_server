package ibas.inchelin.web.dto.review;

import ibas.inchelin.domain.review.Keyword;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

//@Data
//public class ReviewResponse {
//    private Long reviewId;
//    private Double rating;
//    private List<MenuNamePriceResponse> eatingMenus;
//    private List<String> photos;
//    private String content;
//    private List<String> keywords;
//    private LocalDateTime visitedDate;
//    private int likeCount;
//
//    @Data
//    public static class MenuNamePriceResponse {
//        private String name;
//        private int price;
//    }
//}


public record ReviewResponse(
        Long reviewId,
        Long userId,
        Double rating,
        List<MenuNamePriceResponse> eatingMenus,
        List<String> photos,
        String content,
        List<String> keywords,
        String visitedDate,
        int visitCount,
        int likeCount,
        Boolean likedByMe
) {
    public record MenuNamePriceResponse(
            String name,
            int price
    ) {}
}

