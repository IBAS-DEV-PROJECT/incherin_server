package ibas.inchelin.domain.review;

public enum Keyword {
    VALUE_FOR_MONEY("가성비가 좋아요"),
    DELICIOUS("음식이 맛있어요"),
    LARGE_PORTIONS("양이 많아요"),
    KIND_SERVICE("친절해요");

    private final String label;

    Keyword(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
