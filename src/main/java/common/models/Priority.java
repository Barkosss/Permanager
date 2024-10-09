package common.models;

public class Priority {

    // Название приоритета
    public String name;

    /**
     * Приоритетность (Число от >= 0)
     * Приоритетность имеет тип int, так как номер приоритета указывается самим пользователем.
     * Чем номер приоритета ближе к 0, тем приоритетней
     */
    public int priority;

    public Priority(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    // Получить название приоритета
    public String getName() {
        return name;
    }

    // Установить название приоритета
    public void setName(String name) {
        this.name = name;
    }

    // Получить приоритетность
    public int getPriority() {
        return priority;
    }

    // Назначить приоритетность
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
