package ibas.inchelin.web.dto.review;

import lombok.Data;

@Data
public class ReviewWriteRequest {
    private String nickname;
    private Double rating;
    private String content;
}
