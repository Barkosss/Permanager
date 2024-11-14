package common.models;

import common.exceptions.MemberNotFoundException;
import common.repositories.UserRepository;

import java.util.List;

public class InteractionConsole implements Interaction {

    long userID;

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

    public InteractionConsole() {
        this.userID = 0L;
        this.platform = Platform.CONSOLE;
    }

    public Interaction setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    public User getUser(long userId) {
        try {
            return userRepository.findById(userId);
        } catch(MemberNotFoundException err) {
            return null;
        }

    }

    public long getUserID() {
        return userID;
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

    @Override
    public String toString() {

        return "InteractionConsole({"
                + "Platform=" + platform
                + "\nMessage=" + message
                + "\nInline=" + inline
                + "\nArguments=" + arguments
                + "})";
    }
}