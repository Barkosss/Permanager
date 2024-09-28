package constructors;

import java.util.ArrayList;
import java.util.Date;

public class Task {

    // Идентификатор задачи
    public long id;

    // Название задачи
    public String title;

    // Описание задачи
    public String description;

    // Плановое время начала задачи
    public Date timeStart;

    // Плановое время завершения задачи
    public Date timeEnd;

    // Время создания задачи
    public Date createdAt;

    // Состояние, напомнить ли о задаче в плановое время начала
    public boolean isRemind;

    // Список ответственны за задачу
    public ArrayList<Member> property;

    // Приоритет задачи
    public int priority;

    // Статус задачи (Например, "Идея", "В процессе", "Выполнено", "На рассмотрении")
    public String status;

    // Список тэгов задачи
    public ArrayList<String> tags;

    // Состояние, выполнена ли задача
    public boolean isCompleted;

    // Конструктор задачи
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.isCompleted = false;
    }

    /*
    public void help() {
        // ...
    }

    public void create() {
        // ...
    }

    public void edit() {
        // ...
    }

    public void remove() {
        // ...
    }

    public void list() {
        // ...
    }

    public void clear() {
        // ...
    }
    */
};