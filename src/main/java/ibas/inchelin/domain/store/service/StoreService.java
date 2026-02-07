package ibas.inchelin.domain.store.service;

import ibas.inchelin.domain.store.Category;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.web.dto.store.StoreInfoResponse;
import ibas.inchelin.web.dto.store.StoreListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    // 가게 목록 조회
    public StoreListResponse getStoreList(String categoryStr) {
        Category category = null;
        if (categoryStr != null && !categoryStr.isBlank()) {
            category = Category.fromString(categoryStr);
        }

        return new StoreListResponse(storeRepository.findStoreListWithStatistics(category));
    }

    // 가게 상세 정보 조회
    public StoreInfoResponse getStoreInfo(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

        return new StoreInfoResponse(
                store.getId(),
                store.getPlaceName(),
                store.getCategoryName().displayName(),
                store.getPhone(),
                store.getRoadAddressName()
        );
    }
}
