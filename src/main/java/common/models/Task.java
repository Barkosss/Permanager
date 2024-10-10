package common.models;

import java.time.LocalDate;
import java.util.List;

public class Task {

    // Идентификатор задачи
    public long id;

    // Название задачи
    public String title;

    // Описание задачи
    public String description;

    // Плановое время начала задачи
    public LocalDate timeStart;

    // Плановое время завершения задачи
    public LocalDate timeEnd;

    // Время создания задачи
    public LocalDate createdAt;

    // Состояние, напомнить ли о задаче в плановое время начала
    public boolean isNeedRemind;

    // Список ответственных за задачу
    public List<Member> responsible;

    // Приоритет задачи
    public Priority priority;

    // Статус задачи
    public Status status;

    // Список тэгов задачи
    public List<String> tags;

    // Состояние, выполнена ли задача
    public boolean isCompleted;

    // Конструктор задачи
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.isCompleted = false;
    }

    // Получить ID задачи
    public long getId() {
        return id;
    }

    // Установить ID задачи
    public void setId(long id) {
        this.id = id;
    }

    // Получить заголовок
    public String getTitle() {
        return title;
    }

    // Назначить заголовок
    public void setTitle(String title) {
        this.title = title;
    }

    // Получить описание
    public String getDescription() {
        return description;
    }

    // Назначить описание
    public void setDescription(String description) {
        this.description = description;
    }

    // Получить время начала задачи
    public LocalDate getTimeStart() {
        return timeStart;
    }

    // Назначить время начала задачи
    public void setTimeStart(LocalDate timeStart) {
        this.timeStart = timeStart;
    }

    // Получить время окончания задачи
    public LocalDate getTimeEnd() {
        return timeEnd;
    }

    // Назначить время окончания задачи
    public void setTimeEnd(LocalDate timeEnd) {
        this.timeEnd = timeEnd;
    }

    // Получить дату создания
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    // Назначить дату создания
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    // Получить, надо ли напомнить о задаче
    public boolean isNeedRemind() {
        return isNeedRemind;
    }

    // Назначить напоминание
    public void setRemind(boolean remind) {
        isNeedRemind = remind;
    }

    // Получить список ответственных
    public List<Member> getResponsible() {
        return responsible;
    }

    // Установить список назначенных
    public void setResponsible(List<Member> responsible) {
        this.responsible = responsible;
    }

    // Получить приоритет задачи
    public Priority getPriority() {
        return priority;
    }

    // Назначить приоритет
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    // Получить статус задачи
    public Status getStatus() {
        return status;
    }

    // Назначить статус
    public void setStatus(Status status) {
        this.status = status;
    }

    // Получить список тэгов
    public List<String> getTags() {
        return tags;
    }

    // Назначить список тэгов
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Узнать, выполнена ли задача
    public boolean isCompleted() {
        return isCompleted;
    }

    // Ответить выполнение задачи (или отменить выполнение)
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}