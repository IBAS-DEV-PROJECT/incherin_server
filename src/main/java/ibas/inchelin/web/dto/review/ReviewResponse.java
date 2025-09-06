package ibas.inchelin.web.dto.review;

import java.util.List;

public record ReviewResponse(
        Long reviewId,
        Long userId,
        Double rating,
        List<String> eatingMenus,
        List<String> photos,
        String content,
        List<String> keywords,
        String visitedDate,
        int visitCount,
        int likeCount,
        Boolean likedByMe
) {}

