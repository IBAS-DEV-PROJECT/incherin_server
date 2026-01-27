package ibas.inchelin.domain.store;

public enum Category {
    KOREAN("한식"),        // 한식
    CHINESE("중식"),       // 중식
    JAPANESE("일식"),      // 일식
    WESTERN("양식"),       // 양식
    OTHER("기타");        // 기타

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
