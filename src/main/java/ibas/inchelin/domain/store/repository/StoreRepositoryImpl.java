package ibas.inchelin.domain.store.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ibas.inchelin.domain.store.Category;
import ibas.inchelin.web.dto.store.StoreListResponse;
import jakarta.persistence.EntityManager;

import java.util.List;

import static ibas.inchelin.domain.review.entity.QReview.review;
import static ibas.inchelin.domain.store.entity.QStore.store;

public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * IMDB 가중 평균(Weighted Rating) 파라미터
     * Score = v/(v+m) · R + m/(v+m) · C
     *
     * v: 해당 가게의 리뷰 수
     * R: 해당 가게의 평균 평점
     * m: 최소 신뢰 리뷰 수 (이 값보다 리뷰가 적으면 전체 평균 C 쪽으로 수렴)
     * C: 리뷰 평균 평점 (카테고리 조건 시 해당 카테고리 내 평균, 없으면 전체 평균)
     */
    private static final double M = 2.0;

    public StoreRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<StoreListResponse.StoreListItemResponse> findStoreListWithStatistics(Category category) {

        // v: 리뷰 수
        NumberExpression<Long> v = review.count();

        // R: 해당 가게의 평균 평점 (리뷰 없으면 0.0)
        NumberExpression<Double> R = review.rating.avg().coalesce(0.0);

        // C: 리뷰 평균 평점 (카테고리 조건이 있으면 해당 카테고리 내 평균, 없으면 전체 평균)
        NumberExpression<Double> C = category != null
                ? Expressions.asNumber(
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .innerJoin(review.store, store)
                                .where(store.categoryName.eq(category)))
                : Expressions.asNumber(
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review));

        // IMDB Weighted Rating: Score = v/(v+m) · R + m/(v+m) · C
        NumberExpression<Double> weightedRating =
                v.castToNum(Double.class)
                        .divide(v.castToNum(Double.class).add(M))
                        .multiply(R)
                        .add(
                                Expressions.asNumber(M)
                                        .divide(v.castToNum(Double.class).add(M))
                                        .multiply(C)
                        )
                        .castToNum(Double.class);

        return queryFactory
                .select(Projections.constructor(StoreListResponse.StoreListItemResponse.class,
                        store.id,
                        store.placeName,
                        store.categoryName,
                        store.thumbnail,
                        review.rating.avg().coalesce(0.0),
                        v,
                        weightedRating // 가중 평균 (정렬용, DTO에서 무시)
                ))
                .from(store)
                .leftJoin(review).on(review.store.id.eq(store.id))
                .where(categoryEq(category))
                .groupBy(store.id)
                .orderBy(weightedRating.desc())
                .fetch();
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? store.categoryName.eq(category) : null;
    }
}
