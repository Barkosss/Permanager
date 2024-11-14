package common.repositories;

import common.models.User;
import common.exceptions.MemberNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    public Map<Long, User> users;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    // Создать пользователя в памяти
    public void create(long userId) {
        if (users.containsKey(userId)) return;
        users.put(userId, new User(userId));
    }

    // Найти пользователя по ID
    public User findById(long userId) throws MemberNotFoundException {
        User user;
        if ((user = users.get(userId)) != null) {
            return user;
        }
        throw new MemberNotFoundException();
    }

    // Существует ли пользователь
    public boolean existsById(long userId) {
        return users.get(userId) != null;
    }
}
