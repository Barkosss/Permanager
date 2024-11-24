package common.models;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import common.exceptions.MemberNotFoundException;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;

import java.util.List;

public class InteractionTelegram implements Interaction {

    public TelegramBot telegramBot;

    public long timestampBotStart;

    // Платформа: Terminal, Telegram, Discord
    Platform platform;

    // Для Telegram - Chat ID, для Discord - User ID
    long userId;

    // Полное сообщение
    String message;

    // Объект сообщения (Для Telegram)
    SendMessage sendMessage;

    // Выводить в одну строку или нет
    boolean inline;

    // Массив аргументов в сообщении (разделитель - пробел)
    List<String> arguments;

    // ...
    UserRepository userRepository;

    // ...
    ServerRepository serverRepository;

    public InteractionTelegram(TelegramBot telegramBot, long timestampBotStart) {
        this.telegramBot = telegramBot;
        this.timestampBotStart = timestampBotStart;
        this.platform = Platform.TELEGRAM;
    }

    public Interaction setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    public Interaction setServerRepository(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
        return this;
    }

    public User getUser(long userId) {
        try {
            return userRepository.findById(userId);
        } catch (MemberNotFoundException err) {
            return null;
        }

    }

    public Platform getPlatform() {
        return platform;
    }

    public long getUserId() {
        return userId;
    }

    public Interaction setUserId(long userId) {
        this.userId = userId;
        createSendMessage();
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Interaction setMessage(String message) {
        this.message = message;
        createSendMessage();
        return this;
    }

    public SendMessage getSendMessage() {
        return sendMessage;
    }

    public boolean getInline() {
        return inline;
    }

    public Interaction setInline(boolean inline) {
        this.inline = inline;
        return this;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public Interaction setArguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    @Override
    public String toString() {

        return "InteractionConsole({"
                + "Token=" + telegramBot
                + "; TIMESTAMP_BOT_START=" + timestampBotStart
                + "; Platform=" + platform
                + "; userId=" + userId
                + "; Message=" + message
                + "; Inline=" + inline
                + "; arguments=" + arguments
                + "})";
    }

    private void createSendMessage() {
        if (this.userId == 0 || this.message == null) {
            return;
        }

        this.sendMessage = new SendMessage(userId, message);
    }
}