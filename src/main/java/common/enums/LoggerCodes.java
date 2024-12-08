package common.enums;

public enum LoggerCodes {
    // Статусы логгера
    INFO("INFO"),
    DEBUG("DEBUG"),
    ERROR("ERROR"),
    WARNING("WARNING"),
    CRITICAL("CRITICAL");

    private final String status;

    LoggerCodes(String status) {
        this.status = status;
    }

    public String getName() {
        return status;
    }
}
