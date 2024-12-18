package common.models;

import java.time.LocalDate;

public class Warning {

    // Идентификатор предупреждения
    long id;

    // Идентификатор пользователя
    long userId;

    // Идентификатор чата
    long chatId;

    // Идентификатор модератора
    long moderatorId;

    // Причина
    String reason;

    // Длительность
    LocalDate duration;

    // Дата выдачи предупреждения
    LocalDate createdAt;

    public Warning(long chatId, long userId, long moderatorId) {
        this.chatId = chatId;
        this.userId = userId;
        this.moderatorId = moderatorId;
        this.createdAt = LocalDate.now();
    }

    public long getId() {
        return id;
    }

    public Warning setId(long id) {
        this.id = id;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public Warning setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public long getChatId() {
        return chatId;
    }

    public Warning setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public long getModeratorId() {
        return moderatorId;
    }

    public Warning setModeratorId(long moderatorId) {
        this.moderatorId = moderatorId;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public Warning setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public LocalDate getDuration() {
        return duration;
    }

    public Warning setDuration(LocalDate duration) {
        this.duration = duration;
        return this;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public String toString() {
        return String.format("Warning({"
                + "id=%s"
                + "userId=%s"
                + "chatId=%s"
                + "moderationId=%s"
                + "reason=%s"
                + "duration=%s"
                + "})", id, userId, chatId, moderatorId, reason, duration);
    }
}