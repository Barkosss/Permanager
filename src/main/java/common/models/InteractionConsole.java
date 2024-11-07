package common.models;

import java.util.List;

public class InteractionConsole implements Interaction {

    // Платформа: Terminal, Telegram, Discord
    Platform platform;

    // Полное сообщение
    String message;

    // Выводить в одну строку или нет
    boolean inline;

    // Массив аргументов в сообщении (разделитель - пробел)
    List<String> arguments;

    // Объект с информацией об ожидаемых данных
    InputExpectation userInputExpectation;

    public InteractionConsole() {
        this.platform = Platform.CONSOLE;
    }

    public Platform getPlatform() {
        return platform;
    }

    public Interaction setPlatform(Platform platform) {
        this.platform = platform;
        return this;
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

    public InputExpectation getUserInputExpectation() {
        return userInputExpectation;
    }

    @Override
    public String toString() {
        String debugMessage = "InteractionConsole({"
                + "Platform=" + platform
                + "\nMessage=" + message
                + "\nInline=" + inline
                + "\nArguments=" + arguments
                + "\nUserInputExpectation=" + userInputExpectation
                + "})";

        return debugMessage;
    }
}