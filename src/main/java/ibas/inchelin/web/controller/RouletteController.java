package ibas.inchelin.web.controller;

import ibas.inchelin.domain.roulette.service.RouletteService;
import ibas.inchelin.web.dto.roulette.RouletteListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RouletteController {

    private final RouletteService rouletteService;

    // 룰렛 후보 조회
    @GetMapping("/v1/roulette/options")
    public ResponseEntity<RouletteListResponse> getRouletteList() {
        return ResponseEntity.ok(rouletteService.getRouletteList());
    }
}
