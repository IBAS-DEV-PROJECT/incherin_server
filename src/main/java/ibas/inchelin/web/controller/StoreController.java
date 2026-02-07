package ibas.inchelin.web.controller;

import ibas.inchelin.domain.store.service.StoreService;
import ibas.inchelin.web.dto.store.StoreInfoResponse;
import ibas.inchelin.web.dto.store.StoreListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // 가게 목록 조회
    @GetMapping("/v1/shops")
    public ResponseEntity<StoreListResponse> getStoreList(@RequestParam(required = false) String category) {
        log.info("가게 목록 조회 요청 - category: {}", category);
        StoreListResponse storeList = storeService.getStoreList(category);
        log.info("가게 목록 조회 완료 - 가게 수: {}", storeList.stores().size());
        return ResponseEntity.ok(storeList);
    }

    // 가게 상세 정보 조회
    @GetMapping("/v1/shops/{shopId}")
    public ResponseEntity<StoreInfoResponse> getStoreInfo(@PathVariable Long shopId) {
        log.info("가게 상세 정보 조회 요청 - shopId: {}", shopId);
        StoreInfoResponse storeInfo = storeService.getStoreInfo(shopId);
        log.info("가게 상세 정보 조회 완료 - shopName: {}", storeInfo.name());
        return ResponseEntity.ok(storeInfo);
    }
}
