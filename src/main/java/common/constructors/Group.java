package common.constructors;

import java.util.ArrayList;

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
    public ArrayList<Member> members;

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

    // Получить список пользователей
    public ArrayList<Member> getMembers() {
        return members;
    }

    // Назначить список пользователей
    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }
}