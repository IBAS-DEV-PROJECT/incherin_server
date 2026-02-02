package ibas.inchelin.domain.store.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ibas.inchelin.domain.store.Category;
import ibas.inchelin.web.dto.store.StoreListResponse;
import jakarta.persistence.EntityManager;

import java.util.List;

import static ibas.inchelin.domain.review.entity.QReview.review;
import static ibas.inchelin.domain.store.entity.QStore.store;

public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public StoreRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<StoreListResponse.StoreListItemResponse> findStoreListWithStatistics(Category category) {
        return queryFactory
                .select(Projections.constructor(StoreListResponse.StoreListItemResponse.class,
                        store.id,
                        store.placeName,
                        store.categoryName,
                        store.thumbnail,
                        review.rating.avg().coalesce(0.0), // 리뷰가 없을 경우 0.0 처리
                        review.count()
                ))
                .from(store)
                .leftJoin(review).on(review.store.id.eq(store.id))
                .where(categoryEq(category))
                .groupBy(store.id)
                .fetch();
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? store.categoryName.eq(category) : null;
    }
}
