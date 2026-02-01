package ibas.inchelin.domain.store.service;

import ibas.inchelin.domain.review.repository.ReviewRepository;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    // 가게 목록 조회
    public StoreListResponse getStoreList(String category) {
        // 카테고리별 가게 목록 조회
        List<Store> storeList = new ArrayList<>();

        if (category == null || category.isBlank()) {
            storeList = storeRepository.findAll(); // 전체 가게 목록 반환
        } else if (category.equals("korean")) {
            storeList = storeRepository.findByCategoryName(Category.KOREAN);
        } else if (category.equals("chinese")) {
            storeList = storeRepository.findByCategoryName(Category.CHINESE);
        } else if (category.equals("japanese")) {
            storeList = storeRepository.findByCategoryName(Category.JAPANESE);
        } else if (category.equals("western")) {
            storeList = storeRepository.findByCategoryName(Category.WESTERN);
        } else if (category.equals("snack")) {
            storeList = storeRepository.findByCategoryName(Category.SNACK);
        } else if (category.equals("bar")) {
            storeList = storeRepository.findByCategoryName(Category.BAR);
        } else if (category.equals("cafe")) {
            storeList = storeRepository.findByCategoryName(Category.CAFE);
        } else if (category.equals("others")) {
            storeList = storeRepository.findByCategoryName(Category.OTHERS);
        } else {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다.");
        }

        List<StoreListResponse.StoreListItemResponse> storeListResponse = storeList.stream()
                .map(s -> new StoreListResponse.StoreListItemResponse(
                        s.getId(),
                        s.getPlaceName(),
                        s.getCategoryName().displayName(),
                        s.getThumbnail(),
                        reviewRepository.findAverageRatingByStoreId(s.getId()),
                        reviewRepository.countByStoreId(s.getId())
                )).toList();

        return new StoreListResponse(storeListResponse);
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
