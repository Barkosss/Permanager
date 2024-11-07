package common.models;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import common.utils.Validate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Map;

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
    InputExpectation inputExpectation;


    public InteractionTelegram(TelegramBot telegramBot, long timestampBotStart) {
        TELEGRAM_BOT = telegramBot;
        TIMESTAMP_BOT_START = timestampBotStart;
    }

    public InteractionTelegram setUpdate(Content update) {
        this.userID = update.chatId();
        this.message = update.message();
        this.arguments = update.arguments();
        return this;
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
        return inputExpectation;
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