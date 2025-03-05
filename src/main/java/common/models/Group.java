package common.models;

import java.util.List;

public class Group {

    // Наименование группы
    public String name;

    // Список разрешений
    public Permissions permissions;

    // Список ограничений
    public Restrictions restrictions;

    // Иерархия приоритета
    public long priority;

    // Список пользователей, которым назначили группу (Для синхронизации)
    public List<Member> members;

    // Конструктор группы
    public Group(String name, long priority, Permissions permissions) {
        this.name = name;
        this.priority = priority;
        this.permissions = permissions;
    }

    // Получить имя группы
    public String getName() {
        return name;
    }

    // Назначить имя группе
    public void setName(String name) {
        this.name = name;
    }

    // Получить приоритет группы
    public long getPriority() {
        return priority;
    }

    // Назначить приоритет группе
    public void setPriority(long priority) {
        this.priority = priority;
    }

    // Получить разрешения группы
    public Permissions getPermissions() {
        return permissions;
    }

    // Назначить разрешения группе
    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    // Получить ограничения группы
    public Restrictions getRestrictions() {
        return restrictions;
    }

    // Назначить ограничения группы
    public Group setRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
        return this;
    }

    // Получить список пользователей
    public List<Member> getMembers() {
        return members;
    }

    // Назначить список пользователей
    public void setMembers(List<Member> members) {
        this.members = members;
    }
}