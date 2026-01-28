package ibas.inchelin.domain.roulette.service;

import ibas.inchelin.domain.store.entity.Store;
import ibas.inchelin.domain.store.repository.StoreRepository;
import ibas.inchelin.web.dto.roulette.RouletteListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RouletteService {

    private final StoreRepository storeRepository;

    // 룰렛 후보 조회
    public RouletteListResponse getRouletteList() {
        List<Store> storeList = storeRepository.findAll();

        List<RouletteListResponse.RouletteItemResponse> rouletteItems = storeList.stream()
            .map(s -> new RouletteListResponse.RouletteItemResponse(
                    s.getId(),
                    s.getPlaceName()
            )).toList();

        return new RouletteListResponse(rouletteItems);
    }
}
