package common.models;

public class Status {

    // Название статуса
    public String name;

    // Приоритет статуса
    public Priority priority;

    // Конструктор статуса
    public Status(String name, Priority priority) {
        this.name = name;
        this.priority = priority;
    }

    // Получить название статуса
    public String getName() {
        return name;
    }

    // Назначить имя статусу
    public void setName(String name) {
        this.name = name;
    }

    // Получить приоритет статуса
    public Priority getPriority() {
        return priority;
    }

    // Назначить приоритет статусу
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
