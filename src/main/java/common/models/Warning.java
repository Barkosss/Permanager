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
    long moderationId;

    // Причина
    String reason;

    // Длительность
    LocalDate duration;

    public Warning(long id) {
        this.id = id;
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

    public long getModerationId() {
        return moderationId;
    }

    public Warning setModerationId(long moderationId) {
        this.moderationId = moderationId;
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
}