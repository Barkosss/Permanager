package common.models;

import common.exceptions.MemberNotFoundException;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;
import common.utils.JSONHandler;

import java.util.List;

public class InteractionConsole implements Interaction {

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


    public User getUser(long userId) {
        try {
            return userRepository.findById(userId);
        } catch (MemberNotFoundException err) {
            return null;
        }

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

    public Language getLanguageCode() {
        return languageCode;
    }

    public Interaction setLanguageCode(Language languageCode) {
        this.languageCode = languageCode;
        return this;
    }

    public String getLanguageValue(String languageKey) {
        JSONHandler jsonHandler = new JSONHandler();
        return (String) jsonHandler.read("content_" + languageCode.getLang() + ".json", languageKey);
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