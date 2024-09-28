package constructors;

public class Member {

    // Id пользователя (Telegram ID или Discord ID)
    public long id;

    // Список разрешений
    public Permissions permissions;

    // Приоритет пользователя (Совпадает с приоритетностью группы, иначе она -1)
    public int priority;

    // Синхронизация с группой (Для обновления прав доступа)
    public boolean statusSyncGroup;

    // Конструктор пользователя
    public Member(long id, int priority, boolean statusSyncGroup, Permissions permissions) {
        this.id = id;
        this.priority = priority;
        this.statusSyncGroup = statusSyncGroup;
        this.permissions = permissions;
    }

    // Получить ID пользователя
    public long getId() {
        return id;
    }

    // Назначить ID пользователя
    public void setId(long id) {
        this.id = id;
    }

    // Получить статус синхронизации
    public boolean getStatusSyncGroup() {
        return statusSyncGroup;
    }

    // Назначить статус синхронизации
    public void setStatusSyncGroup(boolean statusSyncGroup) {
        this.statusSyncGroup = statusSyncGroup;
    }

    // Получить приоритет пользователя
    public int getPriority() {
        return priority;
    }

    // Назначить приоритет пользователю
    public void setPriority(int priority) {
        this.priority = priority;
    }

    // Получить разрешения пользователя
    public Permissions getPermissions() {
        return permissions;
    }

    // Назначить разрешения пользователю
    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }
};