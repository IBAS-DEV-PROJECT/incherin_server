package ibas.inchelin.web.controller;

import ibas.inchelin.domain.roulette.service.RouletteService;
import ibas.inchelin.web.dto.roulette.RouletteListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RouletteController {

    private final RouletteService rouletteService;

    // 룰렛 후보 조회
    @GetMapping("/v1/roulette/options")
    public ResponseEntity<RouletteListResponse> getRouletteList() {
        log.info("룰렛 후보 조회 요청");
        RouletteListResponse response = rouletteService.getRouletteList();
        log.info("룰렛 후보 조회 성공 - 후보 수: {}", response.options().size());
        return ResponseEntity.ok(response);
    }
}
