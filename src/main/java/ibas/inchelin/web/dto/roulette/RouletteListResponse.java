package ibas.inchelin.web.dto.roulette;

import java.util.List;

public record RouletteListResponse(
        List<RouletteItemResponse> options
) {
    public record RouletteItemResponse(
            Long id,
            String name
    ) {}
}
