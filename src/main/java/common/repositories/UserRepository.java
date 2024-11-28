package common.repositories;

import common.models.User;
import common.exceptions.MemberNotFoundException;
import common.utils.LoggerHandler;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    LoggerHandler logger = new LoggerHandler();
    Map<Long, User> users;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    // Создать пользователя в памяти
    public void create(long userId) {
        if (users.containsKey(userId)) {
            return;
        }
        logger.debug("User by id(" + userId + ") is create");
        users.put(userId, new User(userId));
    }

    // Найти пользователя по ID
    public User findById(long userId) throws MemberNotFoundException {
        if (!users.containsKey(userId)) {
            logger.error("Member by id(" + userId + ") is not found");
            throw new MemberNotFoundException();
        }


        logger.debug("User by id(" + userId + ") is find");
        return users.get(userId);
    }

    // Существует ли пользователь
    public boolean existsById(long userId) {
        return users.get(userId) != null;
    }
}
