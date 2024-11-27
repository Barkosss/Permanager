package common.repositories;

import common.models.Warning;
import common.utils.LoggerHandler;

import java.util.HashMap;
import java.util.Map;

public class WarningRepository {
    LoggerHandler logger = new LoggerHandler();
    Map<Long, Warning> warnings;

    public WarningRepository() {
        this.warnings = new HashMap<>();
    }

    // Создать пользователя в памяти
    public void create(long warningId) {
        if (warnings.containsKey(warningId)) {
            return;
        }
        logger.debug("Warning by id(" + warningId + ") is create");
        warnings.put(warningId, new Warning());
    }

    // Найти пользователя по ID
    public Warning findById(long warningId) {
        Warning warning;
        if ((warning = warnings.get(warningId)) != null) {
            logger.debug("Warning by id(" + warningId + ") is find");
            return warning;
        }
        logger.error("Warning by id(" + warningId + ") is not found");
        return null;
    }

    // Существует ли пользователь
    public boolean existsById(long warningId) {
        return warnings.get(warningId) != null;
    }
}