package common.models;

import java.time.LocalDateTime;

public class Reminder {

    // ID напоминание
    long id;

    // ID пользователя, создавший напоминание
    long userId;

    // Идентификатор чата
    long chatId;

    // Содержимое напоминание
    String content;

    // Дата создание напоминания
    LocalDateTime createdAt;

    // Дата изменения напоминания
    LocalDateTime editAt;

    // Дата, когда надо отправить напоминание
    LocalDateTime sendAt;

    // Платформа, откуда было создано напоминание
    Interaction.Platform platform;

    // Конструктор напоминания
    public Reminder(long id, long chatId, long userId, String content, LocalDateTime createdAt,
                    LocalDateTime sendAt, Interaction.Platform platform) {
        this.id = id;
        this.chatId = chatId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.sendAt = sendAt;
        this.platform = platform;
    }


    // Получить ID напоминания
    public long getId() {
        return id;
    }

    // Получить user ID пользователя
    public long getUserId() {
        return userId;
    }

    // Получить chat ID
    public long getChatId() {
        return chatId;
    }

    // Получить содержимое напоминания
    public String getContent() {
        return content;
    }

    // Назначить новое содержимое
    public Reminder setContent(String content) {
        this.content = content;
        return this;
    }

    // Получить время создания
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Получить дату изменения
    public LocalDateTime getEditAt() {
        return editAt;
    }

    // Получить дату отправки
    public LocalDateTime getSendAt() {
        return sendAt;
    }

    // Назначить новое дату отправки
    public Reminder setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
        return this;
    }

    // Получить платформу
    public Interaction.Platform getPlatform() {
        return platform;
    }
}