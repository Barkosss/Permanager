package common.models;

/**
 *
 */
public class Member {

    // Id пользователя (Telegram ID или Discord ID)
    long id;

    // Список разрешений
    Permissions permissions;

    // Список ограничений
    Restrictions restrictions;

    // Приоритет пользователя (Совпадает с приоритетностью группы, иначе она -1)
    int priority;

    // Синхронизация с группой (Для обновления прав доступа)
    boolean statusSyncGroup;

    // Объект с информацией об ожидаемых данных
    InputExpectation userInputExpectation;

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

    // Получить статус активности
    public boolean getDisabled() {
        return disabled;
    }

    // Изменить статус активности
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    // Получить список ожидаемых входных данных
    public InputExpectation getUserInputExpectation() {
        if (userInputExpectation == null) {
            userInputExpectation = new InputExpectation();
        }
        return userInputExpectation;
    }
}