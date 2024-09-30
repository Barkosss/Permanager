package common.constructors;

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

    // Статус задачи
    public Status status;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRemind() {
        return isRemind;
    }

    public void setRemind(boolean remind) {
        isRemind = remind;
    }

    public ArrayList<Member> getProperty() {
        return property;
    }

    public void setProperty(ArrayList<Member> property) {
        this.property = property;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}