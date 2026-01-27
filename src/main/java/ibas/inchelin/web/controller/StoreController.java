package ibas.inchelin.web.controller;

import ibas.inchelin.domain.store.service.StoreService;
import ibas.inchelin.web.dto.store.StoreInfoResponse;
import ibas.inchelin.web.dto.store.StoreListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // 가게 목록 조회
    @GetMapping("/v1/shops")
    public ResponseEntity<StoreListResponse> getStoreList(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(storeService.getStoreList(category));
    }

    // 가게 상세 정보 조회
    @GetMapping("/v1/shops/{shopId}")
    public ResponseEntity<StoreInfoResponse> getStoreInfo(@PathVariable Long shopId) {
        return ResponseEntity.ok(storeService.getStoreInfo(shopId));
    }
}
