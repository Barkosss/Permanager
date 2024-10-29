package common.models;

import java.util.List;
import java.util.Map;

public class InteractionConsole implements Interaction {

    // Платформа: Terminal, Telegram, Discord
    Platform platform;

    // Полное сообщение
    String message;

    // Массив аргументов в сообщении (разделитель - пробел)
    List<String> arguments;

    // Какой тип ожидается от пользователя
    InteractionTelegram.Type userInputType;

    // Название команды
    String inputCommandName;

    // Какое значение требуется (ключ Map)
    String inputKey;

    // Map значений, которые указываются пользователем
    Map<String, Map<String, String>> expectedInput;

    // Выводить в одну строку или нет
    boolean inline;

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

    public String getInputCommandName() {
        return inputCommandName;
    }

    public Interaction setInputCommandName(String inputCommandName) {
        this.inputCommandName = inputCommandName;
        return this;
    }

    public String getInputKey() {
        return inputKey;
    }

    public Interaction setInputKey(String inputKey) {
        this.inputKey = inputKey;
        return this;
    }

    public void getValue(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = InteractionTelegram.Type.STRING;
    }

    public Interaction setValue(Map<String, Map<String, String>> expectedInput) {
        this.expectedInput = expectedInput;
        return this;
    }

    public void getValueInt(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = InteractionTelegram.Type.INT;
    }

    public Interaction getValueDate(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = InteractionTelegram.Type.DATE;
        return null;
    }

    public Map<String, Map<String, String>> getExpectedInput() {
        return expectedInput;
    }

    public void clearExpectedInput(String commandName) {
        inputKey = inputCommandName = null;
        userInputType = null;
        expectedInput.get(commandName).clear();
    }
}