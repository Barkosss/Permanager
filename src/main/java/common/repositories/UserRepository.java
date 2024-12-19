package common.repositories;

import common.models.User;
import common.exceptions.MemberNotFoundException;
import common.utils.LoggerHandler;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    LoggerHandler logger = new LoggerHandler();
    Map<Long, Map<Long, User>> users;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    // Создать пользователя в памяти
    public User create(long chatId, long userId) {
        if (!users.containsKey(chatId)) {
            users.put(chatId, new HashMap<>());
        }

        if (users.get(chatId).containsKey(userId)) {
            return users.get(chatId).get(userId);
        }

        try {
            User user = new User(userId);
            logger.debug(String.format("User by id(%s) is create in chat by id(%s)", userId, chatId));
            this.users.get(chatId).put(userId, user);
            return user;
        } catch (Exception err) {
            logger.error(String.format("User repository (Create), chatId=%d, userId=%d: %s", chatId, userId, err));
            return null;
        }
    }

    // Найти пользователя по ID
    public User findById(long chatId, long userId) throws MemberNotFoundException {
        if (!users.containsKey(chatId)) {
            users.put(chatId, new HashMap<>());
        }

        if (!users.get(chatId).containsKey(userId)) {
            logger.error(String.format("Member by id(%d) is not found in chat by id(%d)", userId, chatId));
            return create(chatId, userId);
        }


        logger.debug("User by id(" + userId + ") is find in chat by id(" + chatId + ")");
        return users.get(chatId).get(userId);
    }

    // Существует ли пользователь
    public boolean existsById(long chatId, long userId) {
        if (!users.containsKey(chatId)) {
            users.put(chatId, new HashMap<>());
        }

        return users.get(userId) != null;
    }
}
