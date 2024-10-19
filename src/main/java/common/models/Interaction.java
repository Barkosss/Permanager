package common.models;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import common.CommandHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interaction {

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

    // Название команды
    String commandStatusName;

    // Какое значение требуется (ключ Map)
    String commandStatusKey;

    // Map значений, которые указываются пользователем
    Map<String, Map<String, String>> commandStatus;

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

    public Interaction setArguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    public String getCommandStatusName() {
        return commandStatusName;
    }

    public Interaction setCommandStatusName(String commandStatusName) {
        this.commandStatusName = commandStatusName;
        return this;
    }

    public String getCommandStatusKey() {
        return commandStatusKey;
    }

    public Interaction setCommandStatusKey(String commandStatusKey) {
        this.commandStatusKey = commandStatusKey;
        return this;
    }

    public Interaction setCommandStatus(String commandName, String key) {
        this.commandStatusName = commandName;
        this.commandStatusKey = key;
        return this;
    }

    public Map<String, Map<String, String>> getCommandStatus() {
        return commandStatus;
    }

    public Interaction setCommandStatus(Map<String, Map<String, String>> commandStatus) {
        this.commandStatus = commandStatus;
        return this;
    }
}