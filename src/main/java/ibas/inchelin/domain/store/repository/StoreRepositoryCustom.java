package ibas.inchelin.domain.store.repository;

import ibas.inchelin.domain.store.Category;
import ibas.inchelin.web.dto.store.StoreListResponse;

import java.util.List;

public interface StoreRepositoryCustom {

    List<StoreListResponse.StoreListItemResponse> findStoreListWithStatistics(Category category);
}
