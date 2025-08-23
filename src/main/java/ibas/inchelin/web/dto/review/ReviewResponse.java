package ibas.inchelin.web.dto.review;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewResponse {
    private Long reviewId;
    private Double rating;
    private List<MenuNamePriceResponse> eatingMenus;
    private List<String> photos;
    private String content;
    private List<String> keywords;
    private LocalDateTime visitedDate;
    private int likeCount;

    @Data
    public static class MenuNamePriceResponse {
        private String name;
        private int price;
    }
}
