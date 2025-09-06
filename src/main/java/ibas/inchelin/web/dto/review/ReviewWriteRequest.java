package ibas.inchelin.web.dto.review;

import ibas.inchelin.domain.review.Keyword;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReviewWriteRequest {
    private Long storeId;
    private Double rating;
    private String content;
    private List<String> eatingMenus;
    private List<Keyword> keywords;
    private List<MultipartFile> photos;
}
