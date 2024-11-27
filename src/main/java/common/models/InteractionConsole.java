package common.models;

import com.pengrad.telegrambot.model.Message;

import common.exceptions.MemberNotFoundException;
import common.repositories.ReminderRepository;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;
import common.utils.JSONHandler;

import java.util.List;

public class InteractionConsole implements Interaction {

    // Идентификатор чата
    long chatId;

    // Идентификатор пользователя
    long userId;

    // Платформа: Terminal, Telegram, Discord
    Platform platform;

    // Полное сообщение
    String message;

    // Выводить в одну строку или нет
    boolean inline;

    // Массив аргументов в сообщении (разделитель - пробел)
    List<String> arguments;

    // ...
    UserRepository userRepository;

    // ...
    ServerRepository serverRepository;

    // ...
    ReminderRepository reminderRepository;

    // ...
    Content content;

    // Языковой код
    Language languageCode;

    public InteractionConsole() {
        this.userId = 0L;
        this.platform = Platform.CONSOLE;
    }

    public Interaction setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    @Override
    public Interaction setServerRepository(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
        return this;
    }

    @Override
    public Interaction setReminderRepository(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
        return this;
    }

    @Override
    public ReminderRepository getReminderRepository() {
        return reminderRepository;
    }

    public User getUser(long userId) {
        try {
            return userRepository.findById(userId);
        } catch (MemberNotFoundException err) {
            return null;
        }

    }

    public Interaction setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public long getChatId() {
        return chatId;
    }

    public long getUserId() {
        return userId;
    }

    public Platform getPlatform() {
        return platform;
    }

    public String getMessage() {
        return message;
    }

    public InteractionConsole setMessage(String message) {
        this.message = message;
        return this;
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

    public Interaction setContent(Content content) {
        this.content = content;
        return this;
    }

    public Interaction setLanguageCode(Language languageCode) {
        this.languageCode = languageCode;
        return this;
    }

    public String getLanguageValue(String languageKey) {
        JSONHandler jsonHandler = new JSONHandler();
        if (jsonHandler.check("content_" + languageCode.getLang() + ".json", languageKey)) {
            return (String) jsonHandler.read("content_" + languageCode.getLang() + ".json", languageKey);
        }
        return "Undefined";
    }

    @Override
    public String toString() {

        return "InteractionConsole({"
                + "Platform=" + platform
                + "; Message=" + message
                + "; Inline=" + inline
                + "; Arguments=" + arguments
                + "})";
    }
}