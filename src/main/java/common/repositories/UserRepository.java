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
    public void create(long chatId, long userId) {
        if (!users.containsKey(chatId)) {
            users.put(chatId, new HashMap<>());
        }

        if (users.get(chatId).containsKey(userId)) {
            return;
        }
        logger.debug("User by id(" + userId + ") is create");
        users.get(chatId).put(userId, new User(userId));
    }

    // Найти пользователя по ID
    public User findById(long chatId, long userId) throws MemberNotFoundException {
        if (!users.containsKey(chatId)) {
            users.put(chatId, new HashMap<>());
        }

        if (!users.containsKey(userId)) {
            logger.error("Member by id(" + userId + ") is not found in chat by id(" + chatId + ")");
            throw new MemberNotFoundException();
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
