package common.models;

import com.pengrad.telegrambot.TelegramBot;

import java.util.List;
import java.util.Map;

public class Interaction {

    public final TelegramBot TELEGRAM_BOT;

    public String platform;

    long userID;

    String message;

    boolean inline;

    List<String> arguments;

    Map<String, Object> commandStatus;

    public Interaction(TelegramBot telegramBot) {
        TELEGRAM_BOT = telegramBot;
    }

    public String getPlatform() {
        return platform;
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

    public Map<String, Object> getCommandStatus() {
        return commandStatus;
    }

    public Interaction setCommandStatus(Map<String, Object> commandStatus) {
        this.commandStatus = commandStatus;
        return this;
    }
}