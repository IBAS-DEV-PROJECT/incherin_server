package ibas.inchelin.web.dto.store;

import ibas.inchelin.domain.store.Category;

import java.util.List;

public record StoreListResponse(
        List<StoreListItemResponse> stores
) {
    public record StoreListItemResponse(
            Long id,
            String name,
            String category,
            String thumbnail,
            Double averageRating,
            Long reviewCount
    ) {
        // Querydsl용 생성자
        public StoreListItemResponse(
                Long id,
                String name,
                Category category,
                String thumbnail,
                Double averageRating,
                Long reviewCount
        ) {
            this(
                    id,
                    name,
                    category.displayName(),
                    thumbnail,
                    averageRating,
                    reviewCount
            );
        }
    }
}
