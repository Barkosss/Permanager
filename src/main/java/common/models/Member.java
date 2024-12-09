package common.models;

/**
 * Объект участника-модератора у пользователя. Объект хранит в себе:
 * user id: long ()
 * chat id: long ()
 * permissions: Permissions ()
 * restrictions: Restrictions ()
 * priority: int ()
 * statusSyncGroup: boolean ()
 *
 */
public class Member {

    // Id пользователя (Telegram ID или Discord ID)
    long id;

    // Id чата
    long chatId;

    // Список разрешений
    Permissions permissions;

    // Список ограничений
    Restrictions restrictions;

    // Приоритет пользователя (Совпадает с приоритетностью группы, иначе она -1)
    int priority;

    // Синхронизация с группой (Для обновления прав доступа)
    boolean statusSyncGroup;

    // К какой группе присоединён пользователь
    Group group;

    // Выключить участника
    boolean disabled;

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
    public Member setStatusSyncGroup(boolean statusSyncGroup) {
        this.statusSyncGroup = statusSyncGroup;
        return this;
    }

    // Получить приоритет пользователя
    public int getPriority() {
        return priority;
    }

    // Назначить приоритет пользователю
    public Member setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    // Получить разрешения пользователя
    public Permissions getPermissions() {
        return permissions;
    }

    // Назначить разрешения пользователю
    public Member setPermissions(Permissions permissions) {
        this.permissions = permissions;
        return this;
    }

    // Получить статус активности
    public boolean getDisabled() {
        return disabled;
    }

    // Изменить статус активности
    public Member setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }
}