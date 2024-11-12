package common.models;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;

public class InteractionTelegram implements Interaction {

    public final TelegramBot TELEGRAM_BOT;

    public final long TIMESTAMP_BOT_START;

    // Платформа: Terminal, Telegram, Discord
    Platform platform;

    // Для Telegram - Chat ID, для Discord - User ID
    long userID;

    // Полное сообщение
    String message;

    // Объект сообщения (Для Telegram)
    SendMessage sendMessage;

    // Выводить в одну строку или нет
    boolean inline;

    // Массив аргументов в сообщении (разделитель - пробел)
    List<String> arguments;

    // ...
    InputExpectation userInputExpectation;


    public InteractionTelegram(TelegramBot telegramBot, long timestampBotStart) {
        this.TELEGRAM_BOT = telegramBot;
        this.TIMESTAMP_BOT_START = timestampBotStart;
        this.platform = Platform.TELEGRAM;
    }

    public Platform getPlatform() {
        return platform;
    }

    public Interaction setPlatform(Platform platform) {
        this.platform = platform;
        return this;
    }

    public long getUserID() {
        return userID;
    }

    public InteractionTelegram setUserID(long userID) {
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

    public SendMessage getSendMessage() {
        return sendMessage;
    }

    public InteractionTelegram setSendMessage(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
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
        if (userInputExpectation == null) {
            userInputExpectation = new InputExpectation();
        }
        return userInputExpectation;
    }

    @Override
    public String toString() {

        return "InteractionConsole({"
                +  "Token=" + TELEGRAM_BOT
                + "\nTIMESTAMP_BOT_START=" + TIMESTAMP_BOT_START
                + "\nPlatform=" + platform
                + "\nUserID=" + userID
                + "\nMessage=" + message
                + "\nInline=" + inline
                + "\narguments=" + arguments
                + "\nUserInputExpectation=" + userInputExpectation
                + "})";
    }
}