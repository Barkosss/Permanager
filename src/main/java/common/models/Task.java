package common.models;

import common.iostream.OutputHandler;
import org.hibernate.result.Output;

import java.time.LocalDate;
import java.util.List;

public class Task {

    // Идентификатор задачи
    public long id;

    // Идентификатор пользователя
    public long userId;

    // Идентификатор чата
    public long chatId;

    // Название задачи
    public String title;

    // Описание задачи
    public String description;

    // Плановое время завершения задачи
    public LocalDate deadLine;

    // Время создания задачи
    public LocalDateTime createdAt;

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
    public Task(long userId, long chatId, String title, String description) {
        this.chatId = chatId;
        this.userId = userId;
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

    // Получить user ID задачи
    public long getUserId() {
        return userId;
    }

    // Установить user ID задачи
    public void setUserId(long userId) {
        this.userId = userId;
    }

    // Получить chat ID задачи
    public long getChatId() {
        return chatId;
    }

    // Установить chat ID задачи
    public void setChatId(long chatId) {
        this.chatId = chatId;
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

    // Получить время окончания задачи
    public LocalDate getDeadLine() {
        return deadLine;
    }

    // Назначить время окончания задачи
    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    // Получить дату создания
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Назначить дату создания
    public void setCreatedAt(LocalDateTime createdAt) {
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

    public void printTask(OutputHandler output, Interaction interaction){
        output.output(interaction.setMessage("Task " + id + ":\n"
                                                + "Title: " + title + "\n"
                                                + "Description: " + description + "\n"
                                                + "Dedline: " + deadLine + "\n"));
    }
}