package common.repositories;

import common.models.InteractionTelegram;
import common.models.Warning;
import common.utils.LoggerHandler;

import java.util.HashMap;
import java.util.Map;

public class WarningRepository {
    LoggerHandler logger = new LoggerHandler();
    // chatId | userId | warningId | Warning
    Map<Long, Map<Long, Map<Long, Warning>>> warnings;

    public WarningRepository() {
        this.warnings = new HashMap<>();
        logger.info("WarningRepository initialized", true);
    }

    // Создать пользователя в памяти
    public Warning create(Warning warning) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(warning.getChatId())) {
            this.warnings.put(warning.getChatId(), new HashMap<>());
            logger.debug(String.format("Chat by id(%s) is added to warnings repository.", warning.getChatId()));
        }

        long warningId = warning.getId();
        if (!this.warnings.get(warning.getChatId()).containsKey(warning.getUserId())) {
            this.warnings.get(warning.getChatId()).put(warning.getUserId(), new HashMap<>());
            logger.debug(String.format("User by id(%s) is added to warnings repository in chat by id(%s).", warning.getUserId(), warning.getChatId()));
        }
        logger.debug(String.format("Warning by id(%s) is create", warningId));
        this.warnings.get(warning.getChatId()).get(warning.getUserId()).put(warningId, warning);
        return warning;
    }

    // Удалить сервер
    public void remove(Warning warning) {
        long chatId = warning.getChatId();
        long userId = warning.getUserId();

        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(chatId)) {
            this.warnings.put(chatId, new HashMap<>());
            logger.debug(String.format("Chat by id(%s) is added to warnings repository.", chatId));
        }

        if (!this.warnings.get(chatId).containsKey(userId)) {
            this.warnings.get(chatId).put(userId, new HashMap<>());
            logger.debug(String.format("User by id(%s) is added to warnings repository in chat by id(%s).", userId, chatId));
        }

        this.warnings.get(warning.getChatId()).get(warning.getUserId()).remove(warning.getId());
        logger.debug(String.format("Warning by id(%s) is removed for user by id(%s) in chat by id(%s).",
                warning.getId(), warning.getUserId(), warning.getChatId()));
    }

    public void reset(InteractionTelegram interactionTelegram, long chatId) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(chatId)) {
            this.warnings.put(chatId, new HashMap<>());
            logger.debug(String.format("Chat by id(%s) is added to warnings repository.", chatId));
        }

        for (Long userId : this.warnings.get(chatId).keySet()) {
            interactionTelegram.findUserById(userId).resetWarnings(chatId);
            logger.debug(String.format("All warnings reset for user by id(%s) in chat by id(%s).", userId, chatId));
        }

        this.warnings.remove(chatId);
        logger.debug(String.format("All warnings for chat by id(%s) are reset.", chatId));
    }

    public void reset(long chatId, long userId) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(chatId)) {
            this.warnings.put(chatId, new HashMap<>());
            logger.debug(String.format("Chat by id(%s) is added to warnings repository.", chatId));
        }

        if (!this.warnings.get(chatId).containsKey(userId)) {
            this.warnings.get(chatId).put(userId, new HashMap<>());
            logger.debug(String.format("User by id(%s) is added to warnings repository in chat by id(%s).", userId, chatId));
        }

        this.warnings.get(chatId).remove(userId);
        logger.debug(String.format("All warnings for user by id(%s) in chat by id(%s) are reset.", userId, chatId));
    }

    // Найти пользователя по ID
    public Warning findById(long chatId, long userId, long warningId) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(chatId)) {
            this.warnings.put(chatId, new HashMap<>());
            logger.debug(String.format("Chat by id(%s) is added to warnings repository.", chatId));
        }

        if (!this.warnings.get(chatId).containsKey(userId)) {
            this.warnings.get(chatId).put(userId, new HashMap<>());
            logger.debug(String.format("User by id(%s) is added to warnings repository in chat by id(%s).", userId, chatId));
        }

        Warning warning = this.warnings.get(chatId).get(userId).get(warningId);
        logger.debug(String.format("Warning by id(%s), user by id(%s) in chat by id(%s) is find. %s",
                warningId, userId, chatId, warning.toString()));
        return warning;
    }

    // Существует ли пользователь
    public boolean existsById(long chatId, long userId, long warningId) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(chatId)) {
            this.warnings.put(chatId, new HashMap<>());
            logger.debug(String.format("Chat by id(%s) is added to warnings repository.", chatId));
        }

        if (!this.warnings.get(chatId).containsKey(userId)) {
            this.warnings.get(chatId).put(userId, new HashMap<>());
            logger.debug(String.format("User by id(%s) is added to warnings repository in chat by id(%s).", userId, chatId));
        }

        boolean exists = this.warnings.get(chatId).get(userId).containsKey(warningId);
        logger.debug(String.format("Warning by id(%s) exists for user by id(%s) in chat by id(%s): %s", warningId, userId, chatId, exists));
        return exists;
    }
}