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
    public Warning create(long warningId) {
        if (warnings.containsKey(warningId)) {
            return null;
        }
        logger.debug(String.format("Warning by id(%s) is create", warningId));
        Warning warning = new Warning(warningId);
        warnings.put(warningId, warning);
        return warning;
    }

    // Найти пользователя по ID
    public Warning findById(long warningId) {
        Warning warning;
        if ((warning = warnings.get(warningId)) != null) {
            logger.debug(String.format("Warning by id(%s) is find", warningId));
            return warning;
        }
        logger.error(String.format("Warning by id(%s) is not found", warningId));
        return null;
    }

    // Существует ли пользователь
    public boolean existsById(long warningId) {
        return warnings.get(warningId) != null;
    }
}