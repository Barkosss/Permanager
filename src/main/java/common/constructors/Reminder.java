package common.constructors;

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


    // ...
    public long getId() {
        return id;
    }

    // ...
    public void setId(long id) {
        this.id = id;
    }

    // ...
    public String getContent() {
        return content;
    }

    // ...
    public void setContent(String content) {
        this.content = content;
    }

    // ...
    public Date getCreatedAt() {
        return createdAt;
    }

    // ...
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // ...
    public Date getSendAt() {
        return sendAt;
    }

    // ...
    public void setSendAt(Date sendAt) {
        this.sendAt = sendAt;
    }
}
