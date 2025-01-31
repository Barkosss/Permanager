package common.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    // ID Discord сервера или Telegram беседы (Общее название - Сервер)
    long id;

    // Список модераторов/администраторов сервера (Общее название - администраторы)
    List<Member> members;

    // Список групп
    List<Group> groups;

    // Стандартные права доступа
    Permissions defaultPermissions;

    // Стандартные ограничения
    Restrictions defaultRestrictions;

    // Список забаненных пользователей
    Map<Long, List<User>> bans;

    // Список замьюченных пользователей
    Map<Long, User> mutes;

    public Server(long id, List<Member> members, Permissions defaultPermissions) {
        this.id = id;
        this.members = members;
        this.defaultPermissions = defaultPermissions;
    }

    // Получение ID сервера
    public long getId() {
        return id;
    }

    // Установить ID сервера
    public void setId(long id) {
        this.id = id;
    }

    // Получение список администраторов
    public List<Member> getMembers() {
        return members;
    }

    // Установить список администраторов
    public void setMembers(List<Member> members) {
        this.members = members;
    }

    // Получение список стандартных прав доступа
    public Permissions getDefaultPermissions() {
        return defaultPermissions;
    }

    // Установить список стандартных прав доступа
    public Server setDefaultPermissions(Permissions defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
        return this;
    }

    // Получение списка стандартных ограничений
    public Restrictions getDefaultRestrictions() {
        return defaultRestrictions;
    }

    // Установить список стандартных ограничений
    public Server setDefaultRestrictions(Restrictions defaultRestrictions) {
        this.defaultRestrictions = defaultRestrictions;
        return this;
    }

    // Получить список забаненных пользователей
    public Map<Long, List<User>> getBans() {
        return bans;
    }

    // Добавить пользователя в список забаненных
    public Server addUserBan(User user) {
        if (this.bans == null) {
            this.bans = new HashMap<>();
        }

        bans.put(user.userId, List.of(user));
        return this;
    }

    // Удалить пользователя из списка забаненных
    public Server removeUserBan(User user) {
        if (this.bans == null) {
            this.bans = new HashMap<>();
        }

        bans.remove(user.userId);
        return this;
    }

    // Получить список замьюченных пользователей
    public Map<Long, User> getMutes() {
        return mutes;
    }

    // Добавить пользователя в список замьюченных
    public Server addUserMute(User user) {
        if (this.mutes == null) {
            this.mutes = new HashMap<>();
        }

        mutes.put(user.userId, user);
        return this;
    }

    // Удалить пользователя из списка замьюченных
    public Server removeUserMute(User user) {
        mutes.remove(user.userId);
        return this;
    }
}