package common.repositories;

import common.models.Warning;
import common.utils.LoggerHandler;

import java.util.HashMap;
import java.util.Map;

public class WarningRepository {
    LoggerHandler logger = new LoggerHandler();
    Map<Long, Map<Long, Warning>> warnings;

    public WarningRepository() {
        this.warnings = new HashMap<>();
    }

    // Создать пользователя в памяти
    public Warning create(Warning warning) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(warning.getChatId())) {
            this.warnings.put(warning.getChatId(), new HashMap<>());
        }

        long warningId = warning.getId();
        if (this.warnings.containsKey(warningId)) {
            return null;
        }
        logger.debug(String.format("Warning by id(%s) is create", warningId));
        this.warnings.get(warning.getChatId()).put(warningId, warning);
        return warning;
    }

    // Удалить сервер
    public void remove(long chatId, long warningId) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        this.warnings.get(chatId).remove(warningId);
    }

    // Найти пользователя по ID
    public Warning findById(long chatId, long warningId) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        Warning warning = this.warnings.get(chatId).get(warningId);
        logger.debug(String.format("Warning by id(%s) is find. %s", warningId, warning.toString()));
        return warning;
    }

    // Существует ли пользователь
    public boolean existsById(long chatId, long warningId) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        return this.warnings.get(warningId) != null;
    }
}