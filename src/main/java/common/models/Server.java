package common.models;

import common.commands.custom.HelpCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Server {

    // ID Discord сервера или Telegram беседы (Общее название - Сервер)
    private long id;

    // ID владельца
    private long ownerId;

    // Список модераторов/администраторов сервера (Общее название - администраторы)
    private Map<Long, Member> members;

    // Список групп
    private Map<String, Group> groups;

    // Стандартные права доступа
    private Permissions defaultPermissions;

    // Стандартные ограничения
    private Restrictions defaultRestrictions;

    // Состояние модерационных команд
    private Map<String, Boolean> moderationCommands;

    // Список забаненных пользователей
    private Map<Long, List<User>> bans;

    // Список замьюченных пользователей
    private Map<Long, User> mutes;

    public Server(long id, Map<Long, Member> members, Permissions defaultPermissions) {
        this.id = id;
        this.members = members;
        this.defaultPermissions = defaultPermissions;
        this.moderationCommands = initModerationCommands();
    }

    // Инициализация moderationCommands
    private Map<String, Boolean> initModerationCommands() {
        HelpCommand helpCommand = new HelpCommand();

        return helpCommand.methods.keySet()
                .stream()
                .collect(Collectors.toMap(commandName -> commandName, commandName -> true));
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
    public Map<Long, Member> getMembers() {
        return members;
    }

    // Получить пользователя по ID
    public Member getMember(long id) {
        return members.get(id);
    }

    // Проверить наличие модератора по ID
    public boolean hasMember(long id) {
        return members.containsKey(id);
    }

    // Установить список администраторов
    public void setMembers(Map<Long, Member> members) {
        this.members = members;
    }

    // Добавить нового модератора
    public Server addModerator(Member member) {
        this.members.put(member.getId(), member);
        return this;
    }

    // Убрать пользователя из модераторов
    public boolean removeModerator(long moderatorId) {
        try {
            this.members.remove(moderatorId);
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    // Получить все группы
    public Map<String, Group> getGroups() {
        return groups;
    }

    // Получить группу по названию
    public Group getGroup(String name) {
        return groups.get(name);
    }

    // Проверить наличие группы по названию
    public boolean hasGroup(String name) {
        return groups.containsKey(name);
    }

    // Установить новый набор групп
    public Server setGroups(Map<String, Group> groups) {
        this.groups = groups;
        return this;
    }

    // Добавить новую группу
    public Server addGroup(Group group) {
        this.groups.put(group.getName(), group);
        return this;
    }

    // Удалить группу
    public boolean removeGroup(String name) {
        try {
            this.groups.remove(name);
            return true;
        } catch (Exception err) {
            return false;
        }
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
        if (defaultRestrictions == null) {
            defaultRestrictions = new Restrictions();
        }
        return defaultRestrictions;
    }

    // Установить список стандартных ограничений
    public Server setDefaultRestrictions(Restrictions defaultRestrictions) {
        this.defaultRestrictions = defaultRestrictions;
        return this;
    }

    // Получить список модерационных команд
    public Map<String, Boolean> getModerationCommands() {
        return moderationCommands;
    }

    // Установить список модерационных команд
    public Server setModerationCommands(Map<String, Boolean> moderationCommands) {
        this.moderationCommands = moderationCommands;
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