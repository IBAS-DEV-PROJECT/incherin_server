package ibas.inchelin.domain.store.service;

import ibas.inchelin.domain.store.Category;
import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.web.dto.store.StoreInfoResponse;
import ibas.inchelin.web.dto.store.StoreListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    // 가게 목록 조회
    public StoreListResponse getStoreList(String category) {
        // 카테고리별 가게 목록 조회
        List<Store> storeList = new ArrayList<>();

        if (category == null || category.isBlank()) {
            // 전체 가게 목록 반환
            storeList = storeRepository.findAll();
        } else if (category.equals("한식")) {
            storeList = storeRepository.findByCategory(Category.KOREAN);
        } else if (category.equals("중식")) {
            storeList = storeRepository.findByCategory(Category.CHINESE);
        } else if (category.equals("일식")) {
            storeList = storeRepository.findByCategory(Category.JAPANESE);
        } else if (category.equals("양식")) {
            storeList = storeRepository.findByCategory(Category.WESTERN);
        } else {
            storeList = storeRepository.findByCategory(Category.OTHER);
        }

        List<StoreListResponse.StoreListItemResponse> storeListResponse = storeList.stream()
                .map(s -> new StoreListResponse.StoreListItemResponse(
                        s.getId(),
                        s.getStoreName(),
                        s.getCategory().displayName(),
                        s.getThumbnail()
                )).toList();

        return new StoreListResponse(storeListResponse);
    }

    // 가게 상세 정보 조회
    public StoreInfoResponse getStoreInfo(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

        return new StoreInfoResponse(
                store.getId(),
                store.getStoreName(),
                store.getCategory().displayName(),
                store.getPhone(),
                store.getAddress()
        );
    }
}
