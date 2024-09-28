package constructors;

public class Status {

    // Название статуса
    public String name;

    // Приоритет статуса
    public int priority;

    // Конструктор статуса
    public Status(String name, int priority) {
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
    public int getPriority() {
        return priority;
    }

    // Назначить приоритет статусу
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
