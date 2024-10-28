package common.models;

import java.util.List;

public class Update {

    long chatId;

    String message;

    long createdAt;

    // Массив аргументов в сообщении (разделитель - пробел)
    List<String> arguments;

    public Update create(com.pengrad.telegrambot.model.Update update) {
        this.chatId = update.message().chat().id();
        this.message = update.message().text();
        this.arguments = List.of(update.message().text().split(" "));
        return this;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
