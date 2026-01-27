package ibas.inchelin.web.controller;

import ibas.inchelin.domain.store.service.StoreService;
import ibas.inchelin.web.dto.store.StoreListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/v1/shops")
    public ResponseEntity<StoreListResponse> getStoreList(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(storeService.getStoreList(category));
    }
}
