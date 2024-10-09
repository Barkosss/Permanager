package common.models;

import java.util.Date;

public class Reminder {

    // ID напоминание
    public long id;

    // ID пользователя, создавший напоминание
    public long userID;

    // Содержимое напоминание
    public String content;

    // Дата создание напоминания
    public Date createdAt;

    // Дата, когда надо отправить напоминание
    public Date sendAt;

    // Конструктор напоминания
    public Reminder(String content, Date createdAt, Date sendAt) {
        this.content = content;
        this.createdAt = createdAt;
        this.sendAt = sendAt;
    }


    // Получить ID напоминания
    public long getId() {
        return id;
    }

    // Установить ID напоминания
    public void setId(long id) {
        this.id = id;
    }

    // Получить содержимое напоминания
    public String getContent() {
        return content;
    }

    // Установить содержимое напоминания
    public void setContent(String content) {
        this.content = content;
    }

    // Получить время создания
    public Date getCreatedAt() {
        return createdAt;
    }

    // Установить время создания
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Получить дату отправки
    public Date getSendAt() {
        return sendAt;
    }

    // Назначить дату отправкин
    public void setSendAt(Date sendAt) {
        this.sendAt = sendAt;
    }
}
