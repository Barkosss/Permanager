package common.models;

import java.time.LocalDateTime;

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
    LocalDateTime duration;

    // Дата выдачи предупреждения
    LocalDateTime createdAt;

    public Warning(long chatId, long userId, long moderatorId) {
        this.chatId = chatId;
        this.userId = userId;
        this.moderatorId = moderatorId;
        this.createdAt = LocalDateTime.now();
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

    public LocalDateTime getDuration() {
        return duration;
    }

    public Warning setDuration(LocalDateTime duration) {
        this.duration = duration;
        return this;
    }

    public LocalDateTime getCreatedAt() {
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