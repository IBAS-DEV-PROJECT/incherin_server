package ibas.inchelin.domain.store;

public enum Category {
    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    SNACK("분식"),
    BAR("술/안주"),
    CAFE("카페"),
    OTHERS("기타");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    // 문자열을 입력받았을 때 해당 카테고리 반환
    public static Category fromString(String category) {
        switch (category.toLowerCase()) {
            case "korean":
                return KOREAN;
            case "chinese":
                return CHINESE;
            case "japanese":
                return JAPANESE;
            case "western":
                return WESTERN;
            case "snack":
                return SNACK;
            case "bar":
                return BAR;
            case "cafe":
                return CAFE;
            case "others":
                return OTHERS;
            default:
                throw new IllegalArgumentException("유효하지 않은 카테고리입니다.");
        }
    }
}
