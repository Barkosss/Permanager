package common.enums;

public enum ErrorCategories {
    INFO("INFO"),
    DEBUG("DEBUG"),
    ERROR("ERROR"),
    WARNING("WARN"),
    FATAL("FATAL"),
    TRACE("TRACE");

    private final String category;

    ErrorCategories(String category) {
        this.category = category;
    }

    public String getValue() {
        return category;
    }
}
