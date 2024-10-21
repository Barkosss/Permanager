package common.models;

import com.pengrad.telegrambot.TelegramBot;

import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Interaction {

    enum Type {
        INT,
        STRING,
        DATE
    }

    public final TelegramBot TELEGRAM_BOT;

    // Платформа: Terminal, Telegram, Discord
    public String platform;

    // Для Telegram - Chat ID, для Discord - User ID
    long userID;

    // Полное сообщение
    String message;

    // Выводить в одну строку или нет
    boolean inline;

    // Массив аргументов в сообщении (разделитель - пробел)
    List<String> arguments;

    // Какой тип ожидается от пользователя
    Type userInputType;

    // Название команды
    String inputCommandName;

    // Какое значение требуется (ключ Map)
    String inputKey;

    // Map значений, которые указываются пользователем
    Map<String, Map<String, String>> expectedInput;

    public Interaction(TelegramBot telegramBot) {
        TELEGRAM_BOT = telegramBot;
    }

    public String getPlatform() {
        return (platform != null) ? (platform) : ("");
    }

    public Interaction setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public long getUserID() {
        return userID;
    }

    public Interaction setUserID(long userID) {
        this.userID = userID;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Interaction setMessage(String message) {
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

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public String getInputCommandName() {
        return inputCommandName;
    }

    public void setInputCommandName(String inputCommandName) {
        this.inputCommandName = inputCommandName;
    }

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    public void getValue(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = Type.STRING;
    }

    public void getValueInt(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = Type.INT;
    }

    public Interaction getValueDate(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = Type.DATE;
        return this;
    }

    public Map<String, Map<String, String>> getExpectedInput() {
        return expectedInput;
    }

    public void setValue(Map<String, Map<String, String>> expectedInput) {
        this.expectedInput = expectedInput;
    }

    public void clearExpectedInput(String commandName) {
        inputKey = inputCommandName = null;
        userInputType = null;
        expectedInput.get(commandName).clear();
    }

    // Метод для поиска числа в аргументах
    public Optional<Integer> getInt() {
        Validate validate = new Validate();
        Optional<Integer> value;
        for(String argument : arguments) {
            value = validate.isValidInteger(argument);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    // Метод для поиска даты в аргументах
    public Optional<LocalDate> getDate() {
        Validate validate = new Validate();
        Optional<LocalDate> value;
        for(String argument : arguments) {
            value = validate.isValidDate(argument);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }
}